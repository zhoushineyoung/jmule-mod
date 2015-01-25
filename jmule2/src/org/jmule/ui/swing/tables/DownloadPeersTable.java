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
package org.jmule.ui.swing.tables;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.utils.GeneralComparator;
import org.jmule.countrylocator.CountryLocator;
import org.jmule.ui.FlagPack;
import org.jmule.ui.UIConstants;
import org.jmule.ui.swing.models.DownloadPeersModel;
import org.jmule.ui.utils.PeerInfoFormatter;
import org.jmule.ui.utils.SpeedFormatter;

/**
 *
 * Created on Oct 6, 2008
 * @author javajox
 * @version $Revision: 1.4 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class DownloadPeersTable extends JMTable {

	CountryLocator country_locator = CountryLocator.getInstance();
	
	class NickNameTableCellRenderer extends DownloadPeersTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			this.setText(" " + peer.getNickName());
			return this;
		}
	}
	
	class CCTableCellRenderer extends DownloadPeersTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setText(!country_locator.isServiceDown() ? 
				      country_locator.getCountryCode(peer.getIP()) : "Unknown");
		    if( !country_locator.isServiceDown() )
			  this.setToolTipText(country_locator.getCountryName(peer.getIP()));
			return this;
		}
	}
	
	class FlagTableCellRenderer extends DownloadPeersTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			Icon flag_icon = FlagPack.getFlagAsIconByIP(peer.getIP(), FlagPack.FlagSize.S18x25);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			if( flag_icon != null ) this.setIcon(flag_icon);
			if( !country_locator.isServiceDown() )
				 this.setToolTipText(country_locator.getCountryName(peer.getIP()));
			return this;
		}
	}
	
	class AddressTableCellRenderer extends DownloadPeersTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			this.setText(" " + peer.getIP() + " : " + peer.getPort());
			return this;
		}
	}
	
	class DownSpeedTableCellRenderer extends DownloadPeersTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			this.setText(SpeedFormatter.formatSpeed(peer.getDownloadSpeed()) + " ");
			return this;
		}
	}
	
	class UpSpeedTableCellRenderer extends DownloadPeersTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			this.setText(SpeedFormatter.formatSpeed(peer.getUploadSpeed()) + " ");
			return this;
		}
	}
	
	class ClientTableCellRenderer extends DownloadPeersTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			this.setText(" " + PeerInfoFormatter.formatPeerSoftware(peer));
			return this;
		}
	}
	
	class StatusTableCellRenderer extends DownloadPeersTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			//switch(peer.getStatus()) {
			//     case Peer.TCP_SOCKET_CONNECTING : this.setText(" " + "Connecting");
			 //    case Peer.TCP_SOCKET_CONNECTED : this.setText(" " + "Connected");
			//}
			//ugly hack
			Object[] objects = (Object[])value;
			Peer peer = (Peer)objects[0];
			DownloadSession session = (DownloadSession)objects[1];
			this.setText(PeerInfoFormatter.formatPeerStatus(session.getPeerDownloadStatus(peer)));
			return this;
		}
	}
	
	private DownloadSession session;
	
	public DownloadPeersTable(JFrame parent, DownloadSession session) {
		super(parent);
		this.session = session;
		init();
	}
	
	private void init() {
		
		JMTableColumn nick_name = new JMTableColumn();
		nick_name.setIdentifier(UIConstants.DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID);
		nick_name.setModelIndex(DownloadPeersModel.NICK_NAME);
		nick_name.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID));
		nick_name.setHeaderValue("Nickname");
		nick_name.setCellRenderer(new NickNameTableCellRenderer());
		nick_name.setComparator(new GeneralComparator("getNickName"));
		
		table_columns.add(nick_name);
		
		JMTableColumn cc = new JMTableColumn();
		cc.setIdentifier(UIConstants.DOWNLOAD_PEER_LIST_CC_COLUMN_ID);
		cc.setModelIndex(DownloadPeersModel.CC);
		cc.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_PEER_LIST_CC_COLUMN_ID));
		cc.setHeaderValue("CC");
		cc.setCellRenderer(new CCTableCellRenderer());
		
		table_columns.add(cc);
		
		JMTableColumn flag = new JMTableColumn();
		flag.setIdentifier(UIConstants.DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID);
		flag.setModelIndex(DownloadPeersModel.FLAG);
		flag.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_PEER_LIST_FLAG_COLUMN_ID));
		flag.setHeaderValue("Flag");
		flag.setCellRenderer(new FlagTableCellRenderer());
		
		table_columns.add(flag);
		
		JMTableColumn address = new JMTableColumn();
		address.setIdentifier(UIConstants.DOWNLOAD_PEER_LIST_IP_COLUMN_ID);
		address.setModelIndex(DownloadPeersModel.ADDRESS);
		address.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_PEER_LIST_IP_COLUMN_ID));
		address.setHeaderValue("IP:Port");
		address.setCellRenderer(new AddressTableCellRenderer());
		
		table_columns.add(address);
		
		JMTableColumn down_speed = new JMTableColumn();
		down_speed.setIdentifier(UIConstants.DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID);
		down_speed.setModelIndex(DownloadPeersModel.DOWN_SPEED);
		down_speed.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_PEER_LIST_DOWN_SPEED_COLUMN_ID));
		down_speed.setHeaderValue("Down speed");
		down_speed.setCellRenderer(new DownSpeedTableCellRenderer());
		down_speed.setComparator(new GeneralComparator("getDownloadSpeed"));
		
		table_columns.add(down_speed);
		
		JMTableColumn up_speed = new JMTableColumn();
		up_speed.setIdentifier(UIConstants.DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID);
		up_speed.setModelIndex(DownloadPeersModel.UP_SPEED);
		up_speed.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_PEER_LIST_UP_SPEED_COLUMN_ID));
		up_speed.setHeaderValue("Up speed");
		up_speed.setCellRenderer(new UpSpeedTableCellRenderer());
		up_speed.setComparator(new GeneralComparator("getUploadSpeed"));
		
		table_columns.add(up_speed);
		
		JMTableColumn client = new JMTableColumn();
		client.setIdentifier(UIConstants.DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID);
		client.setModelIndex(DownloadPeersModel.CLIENT_NAME);
		client.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID));
		client.setHeaderValue("Client");
		client.setCellRenderer(new ClientTableCellRenderer());
		
		table_columns.add(client);
		
		JMTableColumn status = new JMTableColumn();
		status.setIdentifier(UIConstants.DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID);
		status.setModelIndex(DownloadPeersModel.STATUS);
		status.setVisible(_pref.isColumnVisible(UIConstants.DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID));
		status.setHeaderValue("Status");
		status.setCellRenderer(new StatusTableCellRenderer());
		
		table_columns.add(status);
		
		super.buildColumns(new DownloadPeersModel(session));
		
        class PopupListener extends MouseAdapter {
			
			JMenuItem column_setup;
			
			public PopupListener() {
				 
				 column_setup = new JMenuItem("Column setup");
			}
			
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}
			
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}
			
			private void showPopup(MouseEvent e) {
				 
				 if (e.isPopupTrigger()) {
					 
					 JPopupMenu popup_menu = new JPopupMenu();
					
					 popup_menu.add(column_setup);
					 
					 popup_menu.show(e.getComponent(), e.getX(), e.getY());
				 }
			}	
			
		}
		
		//this.addMouseListener(new PopupListener());
		
	}
	
}
