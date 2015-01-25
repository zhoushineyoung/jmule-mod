/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.jmule.ui.swt.tab.main.kad;

import static org.jmule.ui.UIConstants.KAD_CLIENT_DISTANCE_COLUMN_ID;
import static org.jmule.ui.UIConstants.KAD_CLIENT_ID_COLUMN_ID;
import static org.jmule.ui.UIConstants.KAD_TASK_LOOKUP_HASH_COLUMN_ID;
import static org.jmule.ui.UIConstants.KAD_TASK_LOOKUP_INFO_COLUMN_ID;
import static org.jmule.ui.UIConstants.KAD_TASK_TYPE_COLUMN_ID;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.jmule.core.JMuleCore;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadListener;
import org.jmule.core.jkad.JKadManager;
import org.jmule.core.jkad.publisher.PublishKeywordTask;
import org.jmule.core.jkad.publisher.PublishNoteTask;
import org.jmule.core.jkad.publisher.PublishSourceTask;
import org.jmule.core.jkad.publisher.PublishTask;
import org.jmule.core.jkad.publisher.PublisherListener;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTableListener;
import org.jmule.core.jkad.search.KeywordSearchTask;
import org.jmule.core.jkad.search.NoteSearchTask;
import org.jmule.core.jkad.search.SearchListener;
import org.jmule.core.jkad.search.SearchTask;
import org.jmule.core.jkad.search.SourceSearchTask;
import org.jmule.core.jkad.utils.Utils;
import org.jmule.core.utils.Misc;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.common.SashControl;
import org.jmule.ui.swt.tab.MainTab;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.utils.PeerInfoFormatter;

/**
 * Created on Jul 10, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public class KadTab extends MainTab {

	private JMuleCore _core;
	private JMTable<KadContact> contact_list;
	private JMTable<KadTask> kad_task_list;
	private Map<Int128, KadTask> task_map = new ConcurrentHashMap<Int128, KadTask>();
	private JKadManager _jkad;
	private Group routing_table_container;
	
	public KadTab(Composite ctab, JMuleCore core) {		
		super(ctab,"Kad");
		_core = core;
		_jkad = _core.getJKadManager();
		
		
		tab_content.setLayoutData(new GridData(GridData.FILL_BOTH));
		tab_content.setLayout(new FormLayout());
				
		routing_table_container = new Group(tab_content,SWT.NONE);
		Group kad_tasks_container = new Group(tab_content,SWT.NONE);
		routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes"));
		kad_tasks_container.setText(_._("mainwindow.kadtab.kad_tasks"));
		SashControl.createHorizontalSash(30, 70, tab_content, routing_table_container, kad_tasks_container);
		GridLayout grid_layout = new GridLayout(1,false);
		grid_layout.marginWidth = 0;
		grid_layout.marginHeight = 0;
		routing_table_container.setLayout(grid_layout);
		
		Composite buttons_composite = new Composite(routing_table_container, SWT.NONE);
		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		buttons_composite.setLayoutData(g);
		buttons_composite.setLayout(new GridLayout(1,false));
		
		final Button setKadStatus = new Button(buttons_composite, SWT.NONE);
		try {
			setKadStatus.setEnabled(_core.getConfigurationManager().isJKadAutoconnectEnabled());
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
			setKadStatus.setEnabled(false);
		}
		g = new GridData();
		g.widthHint = 150;
		setKadStatus.setLayoutData(g);
		setKadStatus.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (_jkad.isConnected())
					_jkad.disconnect();
				else
					if (_jkad.isConnecting())
						_jkad.disconnect();
					else
						if (_jkad.isDisconnected()) {
							setKadStatus.setEnabled(false);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									_jkad.connect();
								}
							});
						}
							
			}
		});
		
		if (_jkad.isConnected())
			setKadStatus.setText(_._("mainwindow.kadtab.disconnect"));
		if (_jkad.isConnecting())
			setKadStatus.setText(_._("mainwindow.kadtab.stop_connecting"));
		if (_jkad.isDisconnected())
			setKadStatus.setText(_._("mainwindow.kadtab.connect"));
		
		_jkad.addJKadListener(new JKadListener() {
			public void JKadIsConnected() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (tab_content.isDisposed()) return;
						setKadStatus.setEnabled(true);
						setKadStatus.setText(_._("mainwindow.kadtab.disconnect"));
					}
				});
			}

			public void JKadIsConnecting() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (tab_content.isDisposed()) return;
						setKadStatus.setEnabled(true);
						setKadStatus.setText(_._("mainwindow.kadtab.stop_connecting"));
					}
				});
			}

			public void JKadIsDisconnected() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (tab_content.isDisposed()) return;
						setKadStatus.setEnabled(true);
						routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes"));
						setKadStatus.setText(_._("mainwindow.kadtab.connect"));
						
					}
				});
			}
			
		});
		
		contact_list = new JMTable<KadContact>(routing_table_container,SWT.NONE){
			protected int compareObjects(KadContact object1,
					KadContact object2, int columnID, boolean order) {
				int result = 0;
				if (columnID == KAD_CLIENT_ID_COLUMN_ID)
					result = object1.getContactID().toHexString().compareTo(object2.getContactID().toHexString());
				
				if (columnID == KAD_CLIENT_DISTANCE_COLUMN_ID)
					result = object1.getContactDistance().toBinaryString().compareTo(object2.getContactDistance().toBinaryString());
				
				if (!order)
					result = Misc.reverse(result);
				return result;
			}

			protected Menu getPopUpMenu() {
				return null;
			}

			public void updateRow(KadContact kad_contact) {
				setRowImage(kad_contact, KAD_CLIENT_ID_COLUMN_ID, SWTImageRepository.
						                             getImage(PeerInfoFormatter.
						                            		  getPeerStatusKadImage( kad_contact )));
				setRowText(kad_contact, KAD_CLIENT_ID_COLUMN_ID, kad_contact.getContactID().toHexString());
				setRowText(kad_contact, KAD_CLIENT_DISTANCE_COLUMN_ID, kad_contact.getContactDistance().toBinaryString());
			}
			
		};
		contact_list.setLayoutData(new GridData(GridData.FILL_BOTH));
		contact_list.addColumn(SWT.LEFT, KAD_CLIENT_ID_COLUMN_ID, _._("mainwindow.kadtab.contact_list.column.contact_id"), _._("mainwindow.kadtab.contact_list.column.contact_id.desc"), swt_preferences.getColumnWidth(KAD_CLIENT_ID_COLUMN_ID));
		contact_list.addColumn(SWT.LEFT, KAD_CLIENT_DISTANCE_COLUMN_ID, _._("mainwindow.kadtab.contact_list.column.contact_distance"), _._("mainwindow.kadtab.contact_list.column.contact_distance.desc"), swt_preferences.getColumnWidth(KAD_CLIENT_DISTANCE_COLUMN_ID));
		
		_jkad.getRoutingTable().addListener(new RoutingTableListener() {
			public void contactAdded(final KadContact contact) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (tab_content.isDisposed()) return;
						contact_list.addRow(contact);
						routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes_number",contact_list.getItemCount()+""));
					}
					
				});
				
			}

			public void contactRemoved(final KadContact contact) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (tab_content.isDisposed()) return;
						contact_list.removeRow(contact);
						routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes_number",contact_list.getItemCount()+""));
					}
					
				});
			}

			public void contactUpdated(final KadContact contact) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (tab_content.isDisposed()) return;
						contact_list.updateRow(contact);
						routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes_number",contact_list.getItemCount()+""));
					}
					
				});
			}

			public void allContactsRemoved() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (tab_content.isDisposed()) return;
						contact_list.clear();
					}
					
				});
			}
			
		});
		
		for(KadContact contact : _jkad.getRoutingTable().getContacts())
			contact_list.addRow(contact);
		
		routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes_number",contact_list.getItemCount()+""));
		
		kad_tasks_container.setLayout(new FillLayout());
		kad_task_list = new JMTable<KadTask>(kad_tasks_container,SWT.NONE) {

			protected int compareObjects(KadTask object1, KadTask object2,
					int columnID, boolean order) {
				int result = 0;
				if (columnID == KAD_TASK_TYPE_COLUMN_ID)
					result = object1.task_type.compareTo(object2.task_type);
				if (columnID == KAD_TASK_LOOKUP_HASH_COLUMN_ID)
					result = object1.task_id.compareTo(object2.task_id);
				if (columnID == KAD_TASK_LOOKUP_INFO_COLUMN_ID)
					result = object1.task_info.compareTo(object2.task_info);
				if (!order)
					result = Misc.reverse(result);
				return result;
			}

			protected Menu getPopUpMenu() {
				return null;
			}

			public void updateRow(KadTask object) {
				setRowText(object, KAD_TASK_TYPE_COLUMN_ID, object.task_type);
				setRowText(object, KAD_TASK_LOOKUP_HASH_COLUMN_ID, object.task_id);
				setRowText(object, KAD_TASK_LOOKUP_INFO_COLUMN_ID, object.task_info);
			}
		};
		
		kad_task_list.addColumn(SWT.LEFT, KAD_TASK_TYPE_COLUMN_ID, _._("mainwindow.kadtab.task_list.column.task_type"), _._("mainwindow.kadtab.task_list.column.task_type.desc"), swt_preferences.getColumnWidth(KAD_TASK_TYPE_COLUMN_ID));
		kad_task_list.addColumn(SWT.LEFT, KAD_TASK_LOOKUP_HASH_COLUMN_ID, _._("mainwindow.kadtab.task_list.column.task_hash"), _._("mainwindow.kadtab.task_list.column.task_hash.desc"), swt_preferences.getColumnWidth(KAD_TASK_LOOKUP_HASH_COLUMN_ID));
		kad_task_list.addColumn(SWT.LEFT, KAD_TASK_LOOKUP_INFO_COLUMN_ID, _._("mainwindow.kadtab.task_list.column.task_info"), _._("mainwindow.kadtab.task_list.column.task_info.desc"), swt_preferences.getColumnWidth(KAD_TASK_LOOKUP_INFO_COLUMN_ID));
		kad_task_list.setLinesVisible(true);
		
		_jkad.getPublisher().addListener(new PublisherListener() {
			@Override
			public void publishTaskStopped(final PublishTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						removePublishTask(task);
					}
				});
			}
			
			@Override
			public void publishTaskStarted(final PublishTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						addPublishTask(task);
					}
				});
			}
			
			@Override
			public void publishTaskRemoved(final PublishTask task) {
				
			}
			
			@Override
			public void publishTaskAdded(PublishTask task) {
				
			}
		});
		
		
		for(PublishTask task : _jkad.getPublisher().getPublishTasks())
			addPublishTask(task);
		
		_jkad.getSearch().addListener(new SearchListener() {
			
			@Override
			public void searchStopped(final SearchTask search) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						removeSearchTask(search);
					}
				});
			}
			
			@Override
			public void searchStarted(final SearchTask search) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						addSearchTask(search);
					}
				});
			}
			
			@Override
			public void searchRemoved(SearchTask search) {
			}
			
			@Override
			public void searchAdded(SearchTask search) {
			}
		});
		
		for(SearchTask task : _jkad.getSearch().getSearchTasks())
			addSearchTask(task);
		
		_core.getConfigurationManager().addConfigurationListener(new ConfigurationAdapter() {
			public void jkadStatusChanged(boolean newStatus) {
				if (newStatus == false) {
					setKadStatus.setEnabled(false);
				} else
					setKadStatus.setEnabled(true);
			}
		});
	}

	public JMULE_TABS getTabType() {
		return JMULE_TABS.KAD;
	}
	
	public void lostFocus() {
		//System.out.println("KadTab :: lostFocus");
	}

	public void obtainFocus() {
		//System.out.println("KadTab :: obtainFocus");
	}
	
	private class KadTask {
		public String task_type;
		public String task_id;
		public String task_info;
		
		public int hashCode() {
			return (task_id + task_type).hashCode();
		}
		
		public boolean equals(Object object) {
			if (object == null) return false;
			if (!(object instanceof KadTask)) return false;
			KadTask kt = (KadTask)object;
			return task_id.equals(kt.task_id);
		}
	}
	
	private void addPublishTask(PublishTask task) {
		if (task_map.containsKey(task.getPublishID()))
			return;
		KadTask t = new KadTask();
		
		if (task instanceof PublishKeywordTask) {
			t.task_type = _._("mainwindow.kadtab.lookup.type.keyword_publish");
			t.task_info = ((PublishKeywordTask) task).getKeyword();
		}
		
		if (task instanceof PublishSourceTask) {
			t.task_type = _._("mainwindow.kadtab.lookup.type.source_publish");
			t.task_info = Utils.KadFileIDToFileName(task.getPublishID());
		}
		
		if (task instanceof PublishNoteTask) {
			t.task_type = _._("mainwindow.kadtab.lookup.type.note_publish");
			t.task_info = Utils.KadFileIDToFileName(task.getPublishID());
		}
		
		t.task_id = task.getPublishID().toHexString();
		kad_task_list.addRow(t);
		task_map.put(task.getPublishID(), t);
	}

	private void removePublishTask(PublishTask task) {
		Int128 task_id = task.getPublishID();
		if (!task_map.containsKey(task_id))
			return;
		KadTask t = task_map.get(task_id);
		kad_task_list.removeRow(t);
		task_map.remove(task_id);
	}
	
	private void addSearchTask(SearchTask task) {
		if (task_map.containsKey(task.getSearchID()))
			return;
		KadTask t = new KadTask();
		
		if (task instanceof KeywordSearchTask) {
			t.task_type = _._("mainwindow.kadtab.lookup.type.keyword_search");
			t.task_info = ((KeywordSearchTask) task).getKeyword();
		}
		
		if (task instanceof SourceSearchTask) {
			t.task_type = _._("mainwindow.kadtab.lookup.type.source_search");
			t.task_info = Utils.KadFileIDToFileName(task.getSearchID());
		}
		
		if (task instanceof NoteSearchTask) {
			t.task_type = _._("mainwindow.kadtab.lookup.type.note_search");
			t.task_info = Utils.KadFileIDToFileName(task.getSearchID());
		}
		
		t.task_id = task.getSearchID().toHexString();
		kad_task_list.addRow(t);
		task_map.put(task.getSearchID(), t);
	}
	
	private void removeSearchTask(SearchTask task) {
		Int128 task_id = task.getSearchID();
		if (!task_map.containsKey(task_id))
			return;
		KadTask t = task_map.get(task_id);
		kad_task_list.removeRow(t);
		task_map.remove(task_id);
	}
}
