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
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.downloadmanager.PeerDownloadStatus;
import org.jmule.core.downloadmanager.DownloadStatusList.PeerDownloadInfo;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.Peer.PeerSource;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadManagerException;
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
 * Created on Aug 7, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2011/03/27 16:51:29 $
 */
public class DownloadPeerListTab extends CTabItem implements Refreshable {

	private UploadManager upload_manager = JMuleCoreFactory.getSingleton().getUploadManager();
	
	private DownloadSession download_session;
	private DownloadPeerList peers_table;
	private List<Peer> shown_peers = new LinkedList<Peer>();
	private Menu popup_menu;
	
	public DownloadPeerListTab(CTabFolder tabFolder, DownloadSession downloadSession) {
		super(tabFolder, SWT.NONE);
		download_session = downloadSession;
		setText(_._("downloadinfowindow.tab.peerlist.title"));		
		
		Composite content = new Composite(tabFolder,SWT.NONE);
		setControl(content);
		content.setLayout(new GridLayout(1,true));
		peers_table =  new DownloadPeerList(content);
		peers_table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		popup_menu = new Menu(peers_table);
		
		MenuItem column_setup = new MenuItem(popup_menu,SWT.NONE);
		column_setup.setText(_._("downloadinfowindow.tab.peerlist.menu.column_setup"));
		column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				peers_table.showColumnEditorWindow();
			}
		});
	}

	public void refresh() {
		if (isDisposed()) return ;
		
		List<Peer> upload_peers = null;
		UploadSession upload_session = null;
		
		if (upload_manager.hasUpload(download_session.getFileHash())) {
			try {
				upload_session = upload_manager.getUpload(download_session.getFileHash());
				upload_peers = upload_session.getPeers();
			} catch (UploadManagerException e) {
				e.printStackTrace();
				return ;
			}
			
		}
		
		List<Peer> download_peers = download_session.getPeers();
		
		for(Peer peer : shown_peers) {
			boolean remove = false;
			if (!download_session.hasPeer(peer))
				if (upload_session == null)
					remove = true;
				else
					remove = !upload_session.hasPeer(peer);
			if (remove)
				peers_table.removeRow(peer);
		}

		for(Peer peer : download_peers) {
			if (!shown_peers.contains(peer)){
				shown_peers.add(peer);
				peers_table.addRow(peer);
			}
			peers_table.updateRow(peer);
		}
		if (upload_peers != null)
			for(Peer peer : upload_peers) {
				if (!shown_peers.contains(peer)){
					shown_peers.add(peer);
					peers_table.addRow(peer);
				}
				peers_table.updateRow(peer);
			}
		
	}
	
	private class DownloadPeerList extends JMTable<Peer>{		
		
		public DownloadPeerList(Composite content) {
			super(content, SWT.NONE);
			int width;
			
			width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID,   _._("downloadinfowindow.tab.peerlist.column.nickname"), "", width);
			
			width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_CC_COLUMN_ID);
			addColumn(SWT.CENTER,SWTConstants.DOWNLOAD_PEER_LIST_CC_COLUMN_ID, _._("downloadinfowindow.tab.peerlist.column.country_code"), _._("downloadinfowindow.tab.peerlist.column.country_code.desc"), width);
			if (CountryLocator.getInstance().isServiceDown())
				disableColumn(SWTConstants.DOWNLOAD_PEER_LIST_CC_COLUMN_ID);
			
			width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID, 	  	_._("downloadinfowindow.tab.peerlist.column.flag"), "", width);
			if (CountryLocator.getInstance().isServiceDown())
				disableColumn(SWTConstants.DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID);
			
			width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_IP_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.DOWNLOAD_PEER_LIST_IP_COLUMN_ID, 	  	 _._("downloadinfowindow.tab.peerlist.column.address"), "", width);
			
			width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_SOURCE_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.DOWNLOAD_PEER_LIST_SOURCE_COLUMN_ID, 	  	 _._("downloadinfowindow.tab.peerlist.column.source"),  _._("downloadinfowindow.tab.peerlist.column.source.desc"), width);
			
			width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID); 
			addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID, _._("downloadinfowindow.tab.peerlist.column.download_speed"), "", width);
			
			width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID);
			addColumn(SWT.RIGHT, SWTConstants.DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID,   _._("downloadinfowindow.tab.peerlist.column.upload_speed"),  "", width);
			
			width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID); 
			addColumn(SWT.LEFT, SWTConstants.DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID,   _._("downloadinfowindow.tab.peerlist.column.software"), "", width);
			
			width = swt_preferences.getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID,     _._("downloadinfowindow.tab.peerlist.column.status"), "", width);

			updateColumnVisibility();
			updateColumnOrder();
		}

		
		protected int compareObjects(Peer object1, Peer object2, int columnID,
				boolean order) {
			
			if (columnID==SWTConstants.DOWNLOAD_PEER_LIST_IP_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getIP",order);
			
			if (columnID == SWTConstants.DOWNLOAD_PEER_LIST_SOURCE_COLUMN_ID) {
				String source1 = PeerInfoFormatter.peerSourceToString(object1.getSource());
				String source2 = PeerInfoFormatter.peerSourceToString(object2.getSource());
				return source1.compareTo(source2);
			}
			
			if (columnID==SWTConstants.DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getNickName",order);

			if (columnID==SWTConstants.DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getClientSoftware",order);

			if (columnID==SWTConstants.DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getDownloadSpeed",order);

			if (columnID==SWTConstants.DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getUploadSpeed",order);

			if (columnID == SWTConstants.DOWNLOAD_PEER_LIST_CC_COLUMN_ID) {
				String cc1 = CountryLocator.getInstance().getCountryCode(object1.getIP());
				String cc2 = CountryLocator.getInstance().getCountryCode(object2.getIP());
				int result = cc1.compareTo(cc2);
				if (order)
					return result;
				else
					return Misc.reverse(result);
			}
			
			if (columnID == SWTConstants.DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID) {
				String country1 = CountryLocator.getInstance().getCountryName(object1.getIP());
				String country2 = CountryLocator.getInstance().getCountryName(object2.getIP());
				int result = country1.compareTo(country2);
				if (order)
					return result;
				else
					return Misc.reverse(result);
			}

			if (columnID == SWTConstants.DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID) {
				int id1 = toInt(download_session.getPeerDownloadStatus(object1));
				int id2 = toInt(download_session.getPeerDownloadStatus(object2));
				if (download_session.getPeerDownloadStatus(object1)!=null)
					if (download_session.getPeerDownloadStatus(object2)!=null)
				if ((download_session.getPeerDownloadStatus(object1)
						.getStatus() == PeerDownloadStatus.IN_QUEUE)
						&& (download_session.getPeerDownloadStatus(object2)
								.getStatus() == PeerDownloadStatus.IN_QUEUE)) {
					id1 = download_session.getPeerDownloadStatus(object1)
							.getQueueRank();
					id2 = download_session.getPeerDownloadStatus(object2)
							.getQueueRank();
				}
				int result = 0;
				if (id1 > id2)
					result = 1;
				if (id1 < id2)
					result = -1;
				if (!order)
					return Misc.reverse(result);
				return result;

			}
			
			return 0;
		}
		
		private int toInt(PeerDownloadInfo arg) {
			if (arg==null) return 0;
			PeerDownloadStatus object = arg.getStatus();
			if (object == PeerDownloadStatus.ACTIVE)
				return -1;
			if (object == PeerDownloadStatus.ACTIVE_UNUSED)
				return -2;
			if (object == PeerDownloadStatus.CONNECTED)
				return -3;
			if (object == PeerDownloadStatus.DISCONNECTED)
				return -4;
			if (object == PeerDownloadStatus.FILE_STATUS_REQUEST)
				return -5;
			if (object == PeerDownloadStatus.HASHSET_REQUEST)
				return -6;
			if (object == PeerDownloadStatus.IN_QUEUE)
				return -7;
			if (object == PeerDownloadStatus.INACTIVE)
				return -8;
			if (object == PeerDownloadStatus.SLOTREQUEST)
				return -9;
			if (object == PeerDownloadStatus.UPLOAD_REQUEST)
				return -10;
			return 0;
		}

		protected void addRow(Peer object) {
			super.addRow(object);
			
			if (!CountryLocator.getInstance().isServiceDown()) {
				
				Image image = SWTImageRepository.getFlagByAddress(object.getIP(),FlagSize.S25x15);
				
				CountryFlagPainter painter = new CountryFlagPainter(image);
				TableItemCountryFlag table_item_painter = new TableItemCountryFlag(SWTPreferences.getDefaultColumnOrder(SWTConstants.DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID),painter);
				addCustumControl(getItemCount()-1, table_item_painter);
		}
			
		}
		
		public void updateRow(Peer object) {
			String country_code = CountryLocator.getInstance().getCountryCode(object.getIP());
			setRowText(object, SWTConstants.DOWNLOAD_PEER_LIST_CC_COLUMN_ID, country_code);
			
			setRowText(object, SWTConstants.DOWNLOAD_PEER_LIST_IP_COLUMN_ID, object.getIP()+":"+object.getPort());
			PeerSource source = object.getSource();
			
			setRowText(object, SWTConstants.DOWNLOAD_PEER_LIST_SOURCE_COLUMN_ID, PeerInfoFormatter.peerSourceToString(source));
			
			setRowText(object, SWTConstants.DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID, object.getNickName());
			setRowText(object, SWTConstants.DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID, PeerInfoFormatter.formatPeerSoftware(object));

			setRowText(object, SWTConstants.DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID,SpeedFormatter.formatSpeed(object.getDownloadSpeed()));
			setRowText(object, SWTConstants.DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID,SpeedFormatter.formatSpeed(object.getUploadSpeed()));
			
			if (download_session.hasPeer(object)) {
				setRowText(object, SWTConstants.DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID, PeerInfoFormatter.formatPeerStatus(download_session.getPeerDownloadStatus(object)));
			} else {		
				int rank = 0;
				if (upload_manager.getUploadQueue().hasSlotPeer(object)) {
					try {
						rank = upload_manager.getUploadQueue().getPeerPosition(object);
					} catch (UploadQueueException e) {
						e.printStackTrace();
					}
				}
				String str="";
				if (rank!=0) {
					str = Localizer.getString("downloadinfowindow.tab.peerlist.column.status.upload_queue",rank+"");
				} else {
					str = _._("downloadinfowindow.tab.peerlist.column.status.uploading");
				}
					
				setRowText(object, SWTConstants.DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID, str);
				}
		}

		protected Menu getPopUpMenu() {
			return popup_menu;
		}
	}

}
