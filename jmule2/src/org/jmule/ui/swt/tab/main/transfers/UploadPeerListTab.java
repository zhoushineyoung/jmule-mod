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
package org.jmule.ui.swt.tab.main.transfers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.uploadmanager.UploadQueue;
import org.jmule.core.uploadmanager.UploadQueueException;
import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.core.utils.Misc;
import org.jmule.countrylocator.CountryLocator;
import org.jmule.ui.FlagPack.FlagSize;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.common.CountryFlagPainter;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.swt.tables.TableItemCountryFlag;
import org.jmule.ui.utils.PeerInfoFormatter;
import org.jmule.ui.utils.SpeedFormatter;

/**
 * Created on Aug 11, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public class UploadPeerListTab extends CTabItem implements Refreshable{

	private UploadQueue _upload_queue;
	private UploadSession upload_session;
	private UploadPeerList peers_table;
	private List<Peer> shown_peers = new LinkedList<Peer>();
	private Menu popup_menu;
	
	public UploadPeerListTab(CTabFolder tabFolder, UploadSession uploadSession) {
		super(tabFolder, SWT.NONE);
		_upload_queue = JMuleCoreFactory.getSingleton().getUploadManager().getUploadQueue();
		upload_session = uploadSession;
		
		setText(Localizer._("uploadinfowindow.tab.peerlist.title"));
		
		Composite content = new Composite(tabFolder,SWT.NONE);
		setControl(content);
		content.setLayout(new GridLayout(1,true));
		peers_table =  new UploadPeerList(content);
		peers_table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		popup_menu = new Menu(peers_table);
		
		MenuItem column_setup = new MenuItem(popup_menu,SWT.NONE);
		column_setup.setText(_._("uploadinfowindow.tab.peerlist.menu.column_setup"));
		column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				peers_table.showColumnEditorWindow();
			}
		});
		
	}
	
	public void refresh() {
		if (isDisposed()) return ;
		List<Peer> peers = upload_session.getPeers();

		for(Peer peer : shown_peers) {
			if (!upload_session.hasPeer(peer))
				peers_table.removeRow(peer);
		}

		for(Peer peer : peers) {
			if (!shown_peers.contains(peer)){
				shown_peers.add(peer);
				peers_table.addRow(peer);
			}
			peers_table.updateRow(peer);
		}
	}

	private class UploadPeerList extends JMTable<Peer> {

		public UploadPeerList(Composite composite) {
			super(composite, SWT.NONE);
			int width;
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID, _._("uploadinfowindow.tab.peerlist.column.nickname"), "",	SWTPreferences.getInstance().getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID));
			
			width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_CC_COLUMN_ID);
			addColumn(SWT.CENTER, SWTConstants.UPLOAD_PEER_LIST_CC_COLUMN_ID, 		_._("uploadinfowindow.tab.peerlist.column.country_code"), "", 		width );
			if (CountryLocator.getInstance().isServiceDown())
				disableColumn(SWTConstants.UPLOAD_PEER_LIST_CC_COLUMN_ID);
			
			
			width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID, 		_._("uploadinfowindow.tab.peerlist.column.flag"), "", 		width );
			if (CountryLocator.getInstance().isServiceDown())
				disableColumn(SWTConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID);
			
			width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_IP_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_IP_COLUMN_ID, 			_._("uploadinfowindow.tab.peerlist.column.address"), "", width);
			
			width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID);
			addColumn(SWT.RIGHT, SWTConstants.UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID, _._("uploadinfowindow.tab.peerlist.column.up_speed"), "", width);
			
			width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID, _._("uploadinfowindow.tab.peerlist.column.software"), "", 	width);
			
			width = swt_preferences.getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_STATUS_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_STATUS_COLUMN_ID, _._("uploadinfowindow.tab.peerlist.column.status"), "", width);

			updateColumnVisibility();
			updateColumnOrder();
			
		}
		
		public void addRow(Peer object) {
			super.addRow(object);
			if (!CountryLocator.getInstance().isServiceDown()) {
				Image image = SWTImageRepository.getFlagByAddress(object.getIP(),FlagSize.S25x15);
				
				CountryFlagPainter painter = new CountryFlagPainter(image);
				TableItemCountryFlag table_item_painter = new TableItemCountryFlag(SWTPreferences.getDefaultColumnOrder(SWTConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID),painter);
				addCustumControl(getItemCount()-1, table_item_painter);
			}
		}

		protected int compareObjects(Peer object1, Peer object2,
				int columnID, boolean order) {
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getUploadSpeed",order);
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_IP_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getIP",order);
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getNickName",order);

			if (columnID == SWTConstants.UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getClientSoftware",order);
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_CC_COLUMN_ID) {
				String country1 = CountryLocator.getInstance().getCountryCode(object1.getIP());
				String country2 = CountryLocator.getInstance().getCountryCode(object2.getIP());
				int result = country1.compareTo(country2);
				if (order)
					return result;
				else
					return Misc.reverse(result);
			}
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID) {
				String country1 = CountryLocator.getInstance().getCountryName(object1.getIP());
				String country2 = CountryLocator.getInstance().getCountryName(object2.getIP());
				int result = country1.compareTo(country2);
				if (order)
					return result;
				else
					return Misc.reverse(result);
			}
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_STATUS_COLUMN_ID) {
				int id1 = 0;
				try {
					id1 = _upload_queue.getPeerPosition(object1);
				} catch (UploadQueueException e) {
					e.printStackTrace();
				}
				int id2 = 0;
				try {
					id2 = _upload_queue.getPeerPosition(object2);
				} catch (UploadQueueException e) {
					e.printStackTrace();
				}
				
				int result = 0;
				if (id1>id2) result = 1;
				if (id1<id2) result = -1;
				if (!order) return Misc.reverse(result);
				return result;
				
			}
			
			return 0;
		}

		protected Menu getPopUpMenu() {
			return popup_menu;
		}

		public void updateRow(Peer object) {
			String country_code = CountryLocator.getInstance().getCountryCode(
					object.getIP());
			setRowText(object, SWTConstants.UPLOAD_PEER_LIST_CC_COLUMN_ID,
					country_code);
			setRowText(object, SWTConstants.UPLOAD_PEER_LIST_IP_COLUMN_ID,
					object.getIP() + ":" + object.getPort());
			setRowText(object,
					SWTConstants.UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID, object
							.getNickName());
			setRowText(object,
					SWTConstants.UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID,
					PeerInfoFormatter.formatPeerSoftware(object));
			
			setRowText(object, SWTConstants.UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID, SpeedFormatter.formatSpeed(object.getUploadSpeed()));
			
			int rank = 0;
			if (_upload_queue.hasSlotPeer(object))
				try {
					rank = _upload_queue.getPeerPosition(object);
				} catch (UploadQueueException e) {
					e.printStackTrace();
				}
			String str="";
			if (rank!=0) {
				str = Localizer.getString("uploadinfowindow.tab.peerlist.column.status.queue",rank+"");
			} else {
				str = _._("uploadinfowindow.tab.peerlist.column.status.uploading");
			}
			setRowText(object, SWTConstants.UPLOAD_PEER_LIST_STATUS_COLUMN_ID, 	str);

		}
		
	}
	
}
