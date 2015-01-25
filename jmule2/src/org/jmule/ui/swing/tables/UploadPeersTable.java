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
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadQueueException;
import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.core.utils.GeneralComparator;
import org.jmule.core.utils.Misc;
import org.jmule.countrylocator.CountryLocator;
import org.jmule.ui.FlagPack;
import org.jmule.ui.UIConstants;
import org.jmule.ui.swing.models.UploadPeersModel;
import org.jmule.ui.utils.PeerInfoFormatter;
import org.jmule.ui.utils.SpeedFormatter;

/**
 *
 * Created on Oct 7, 2008
 * @author javajox
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2009/11/20 12:23:59 $
 */
public class UploadPeersTable extends JMTable {

	CountryLocator country_locator = CountryLocator.getInstance();
	UploadSession session;
	JMuleCore _core = JMuleCoreFactory.getSingleton();
	UploadManager _upload_manager = _core.getUploadManager();
	
    class NickNameTableCellRenderer extends UploadPeersTableCellRenderer {
    	public Component getTableCellRendererComponent(JTable table, Object value,
    				boolean isSelected, boolean hasFocus, int row, int column) {
    		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    		this.setHorizontalAlignment(SwingConstants.LEFT);
    		this.setText(" " + peer.getNickName());
    		return this;
    	}
    }
      
    class CCTableCellRenderer extends UploadPeersTableCellRenderer {
    	public Component getTableCellRendererComponent(JTable table, Object value,
    				boolean isSelected, boolean hasFocus, int row, int column) {
    		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    		this.setHorizontalAlignment(SwingConstants.CENTER);
    		this.setText(country_locator.getCountryCode(peer.getIP()));
    		this.setToolTipText(country_locator.getCountryName(peer.getIP()));
    		return this;
    	}
    }  
    
    class FlagTableCellRenderer extends UploadPeersTableCellRenderer {
    	public Component getTableCellRendererComponent(JTable table, Object value,
    				boolean isSelected, boolean hasFocus, int row, int column) {
    		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(SwingConstants.CENTER);
    		this.setIcon(FlagPack.getFlagAsIconByIP(peer.getIP(), FlagPack.FlagSize.S18x25));	
    		this.setToolTipText(country_locator.getCountryName(peer.getIP()));
    		return this;
    	}
    }
    
    class IPTableCellRenderer extends UploadPeersTableCellRenderer {
    	public Component getTableCellRendererComponent(JTable table, Object value,
    				boolean isSelected, boolean hasFocus, int row, int column) {
    		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    		this.setHorizontalAlignment(SwingConstants.LEFT);
    		this.setText(" " + peer.getIP() + " : " + peer.getPort());
    		return this;
    	}
    }
    
    /*class DownSpeedTableCellRenderer extends UploadPeersTableCellRenderer {
    	public Component getTableCellRendererComponent(JTable table, Object value,
    				boolean isSelected, boolean hasFocus, int row, int column) {
    		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    			
    		return this;
    	}
    }*/
    
    class UpSpeedTableCellRenderer extends UploadPeersTableCellRenderer {
    	public Component getTableCellRendererComponent(JTable table, Object value,
    				boolean isSelected, boolean hasFocus, int row, int column) {
    		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    		this.setHorizontalAlignment(SwingConstants.RIGHT);
    		this.setText(SpeedFormatter.formatSpeed(peer.getUploadSpeed()) + " ");	
    		return this;
    	}
    }
    
    class ClientTableCellRenderer extends UploadPeersTableCellRenderer {
    	public Component getTableCellRendererComponent(JTable table, Object value,
    				boolean isSelected, boolean hasFocus, int row, int column) {
    		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    		this.setHorizontalAlignment(SwingConstants.LEFT);
    		this.setText(" " + PeerInfoFormatter.formatPeerSoftware(peer));
    		return this;
    	}
    }
    
    class StatusTableCellRenderer extends UploadPeersTableCellRenderer {
    	public Component getTableCellRendererComponent(JTable table, Object value,
    				boolean isSelected, boolean hasFocus, int row, int column) {
    		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    	    Object[] objects = (Object[])value;
    	    UploadSession session = (UploadSession)objects[0];
    	    Peer peer = (Peer)objects[1];
    	    int peer_position = 0;
			try {
				peer_position = _upload_manager.
				             getUploadQueue().getPeerPosition(peer);
			} catch (UploadQueueException e) {
				e.printStackTrace();
			}
    	    boolean is_uploading = (peer_position == 0)?true:false;
    	    this.setHorizontalAlignment(SwingConstants.LEFT);
    	    this.setText(" " + (is_uploading?"Uploading":"Position: " + peer_position));
    		return this;
    	}
    }
    
    // -------------------------------- Comparators -----------------------------------------
    
    class CCComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Peer peer1 = (Peer)o1;
            Peer peer2 = (Peer)o2;
            String cc1 = country_locator.getCountryCode(peer1.getIP());
            String cc2 = country_locator.getCountryCode(peer2.getIP());
			return Misc.compareAllObjects(cc1, cc2, "toString", true);
		}	
    }
    
    public UploadPeersTable(JFrame parent, UploadSession session) {
    	
    	super(parent);
    	
    	this.session = session;
    	
    	JMTableColumn nick_name = new JMTableColumn();
    	nick_name.setIdentifier(UIConstants.UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID);
    	nick_name.setModelIndex(UploadPeersModel.NICK_NAME);
    	nick_name.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID));
    	nick_name.setHeaderValue("Nickname");
    	nick_name.setCellRenderer(new NickNameTableCellRenderer());
    	nick_name.setComparator(new GeneralComparator("getNickName"));
    	
    	table_columns.add(nick_name);
    	
    	JMTableColumn cc = new JMTableColumn();
    	cc.setIdentifier(UIConstants.UPLOAD_PEER_LIST_CC_COLUMN_ID);
    	cc.setModelIndex(UploadPeersModel.CC);
    	cc.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_PEER_LIST_CC_COLUMN_ID));
    	cc.setHeaderValue("CC");
    	cc.setCellRenderer(new CCTableCellRenderer());
    	cc.setComparator(new CCComparator());
    	
    	table_columns.add(cc);
    	
    	JMTableColumn flag = new JMTableColumn();
    	flag.setIdentifier(UIConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID);
    	flag.setModelIndex(UploadPeersModel.FLAG);
    	flag.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID));
    	flag.setHeaderValue("Flag");
    	flag.setCellRenderer(new FlagTableCellRenderer());
    	flag.setComparator(new GeneralComparator("ooops2!"));
    	flag.setComparator(new CCComparator());
    	
    	table_columns.add(flag);
    	
    	JMTableColumn ip = new JMTableColumn();
    	ip.setIdentifier(UIConstants.UPLOAD_PEER_LIST_IP_COLUMN_ID);
    	ip.setModelIndex(UploadPeersModel.IP);
    	ip.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_PEER_LIST_IP_COLUMN_ID));
    	ip.setHeaderValue("IP:Port");
    	ip.setCellRenderer(new IPTableCellRenderer());
    	ip.setComparator(new GeneralComparator("getAddress"));
    	
    	table_columns.add(ip);
    	
    	JMTableColumn up_speed = new JMTableColumn();
    	up_speed.setIdentifier(UIConstants.UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID);
    	up_speed.setModelIndex(UploadPeersModel.UP_SPEED);
    	up_speed.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID));
    	up_speed.setHeaderValue("Up speed");
    	up_speed.setCellRenderer(new UpSpeedTableCellRenderer());
    	up_speed.setComparator(new GeneralComparator("getUploadSpeed"));
    	
    	table_columns.add(up_speed);
    	
    	JMTableColumn client = new JMTableColumn();
    	client.setIdentifier(UIConstants.UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID);
    	client.setModelIndex(UploadPeersModel.CLIENT);
    	client.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID));
    	client.setHeaderValue("Client");
    	client.setCellRenderer(new ClientTableCellRenderer());
    	client.setComparator(new Comparator() {
			public int compare(Object o1, Object o2) {
				String client1 = PeerInfoFormatter.formatPeerSoftware((Peer)o1);
				String client2 = PeerInfoFormatter.formatPeerSoftware((Peer)o2);
				return Misc.compareAllObjects(client1, client2, "toString", true);
			}
    	});
    	
    	table_columns.add(client);
    	
    	JMTableColumn status = new JMTableColumn();
    	status.setIdentifier(UIConstants.UPLOAD_PEER_LIST_STATUS_COLUMN_ID);
    	status.setModelIndex(UploadPeersModel.STATUS);
    	status.setVisible(_pref.isColumnVisible(UIConstants.UPLOAD_PEER_LIST_STATUS_COLUMN_ID));
    	status.setHeaderValue("Status");
    	status.setCellRenderer(new StatusTableCellRenderer());
    	status.setComparator(new Comparator() {
			public int compare(Object o1, Object o2) {
				Object[] objects1 = (Object[])o1;
				Object[] objects2 = (Object[])o2;
				UploadSession session1 = (UploadSession)objects1[0];
				Peer peer1 = (Peer)objects1[1];    
				UploadSession session2 = (UploadSession)objects2[0];
	    	    Peer peer2 = (Peer)objects2[1];
	    	    int peer_position1 = 0;
				try {
					peer_position1 = _upload_manager.getUploadQueue().getPeerPosition(peer1);
				} catch (UploadQueueException e) {
					e.printStackTrace();
				}
	    	    int peer_position2 = 0;
				try {
					peer_position2 = _upload_manager.getUploadQueue().getPeerPosition(peer2);
				} catch (UploadQueueException e) {
					e.printStackTrace();
				}
	    	    boolean is_uploading1 = (peer_position1 == 0)?true:false;
	    	    boolean is_uploading2 = (peer_position2 == 0)?true:false;
	    	    String str1 = is_uploading1?"Uploading":"Position: " + peer_position1;
	    	    String str2 = is_uploading2?"Uploading":"Position: " + peer_position2;
				return Misc.compareAllObjects(str1, str2, "toString", true);
			}   		
    	});
    	
    	table_columns.add(status);
    	
    	super.buildColumns(new UploadPeersModel(session));
    	
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
    
    private void init() {
    	  	
		
		
    	
    }
	
}
