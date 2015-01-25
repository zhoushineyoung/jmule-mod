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
package org.jmule.ui.swing.maintabs.serverlist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerListener;
import org.jmule.ui.swing.maintabs.AbstractTab;
import org.jmule.ui.swing.tables.ServerListTable;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/09/27 14:20:00 $$
 */
public class ServerListTab extends AbstractTab {

   private JSplitPane splitPane;   // splits up the visible area into 2 horizontal parts: top, where the server list is located
                                   // and bottom (server messages and infoes)
   private JSplitPane splitPane2;  // bottom visible area is splitted up into 2 vertical parts: left, server messages, right infoes
   private JScrollPane serverListScrollPane;
   private JScrollPane serverMessagesScrollPane;
   private JScrollPane myInfoScrollPane;
   private GridLayout gridLayout;
   private ServerListTable serverListTable;
   //private MyInfoTable myInfoTable;
   private Info myInfoTable;
   private JTextArea serverMessages;
   private TitledBorder serverListScrollPaneBorder;
   
   private JMuleCore _core = JMuleCoreFactory.getSingleton();
   private ServerManager _server_manager = _core.getServerManager();
	
   public ServerListTab(JFrame parent) {
	   super(parent);
	   initComponents();
	   super.registerRefreshable(serverListTable);
   }
   
   private void initComponents() {   
	   splitPane = new JSplitPane();
	   splitPane2 = new JSplitPane();
	   serverListScrollPane = new JScrollPane();
	   serverMessagesScrollPane = new JScrollPane();
	   myInfoScrollPane = new JScrollPane();
	   gridLayout = new GridLayout(1,1);
	   serverListTable = new ServerListTable(parent); 
	   //myInfoTable = new MyInfoTable();
	   myInfoTable = new Info();
	   serverMessages = new JTextArea();	   
	   splitPane.setDividerLocation(200);
	   splitPane2.setDividerLocation(500);
	   this.setLayout(gridLayout);
	   splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
	   this.add(splitPane);
	   splitPane2.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
	   splitPane.setBottomComponent(splitPane2);
	   
	   //The following declarations are the style for titled border used in the appl.
	   Font titledBorderTextFont = new java.awt.Font("Dialog", 0, 12);
	   Color titledBorderTextColor = Color.BLACK; 
	   Color titledBorderColor = Color.GRAY;
	   LineBorder border = new javax.swing.border.LineBorder(titledBorderColor, 1, true);
	   serverMessages.setEditable(false);
	   //Set up the border and border style for the all titled borders
	   /*TitledBorder titledBorder = 
		   javax.swing.BorderFactory.createTitledBorder(
				   border, 
                   "Unknown", 
                   javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                   javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                   titledBorderTextFont, 
                   titledBorderTextColor);*/
	   
	   //Set up the title border for serverListScrollPane
	  
	   serverListScrollPaneBorder = 
		   javax.swing.BorderFactory.createTitledBorder(
				   border, 
				   "Servers", 
                   javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                   javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                   titledBorderTextFont, 
                   titledBorderTextColor);
	   //TitledBorder serverListScrollPaneBorder = javax.swing.BorderFactory.createTitledBorder("Servers");
	   serverListScrollPane.setBorder(serverListScrollPaneBorder);
	   
	   _server_manager.addServerListListener(new ServerManagerListener() {
          public void autoConnectStarted() {}
          public void autoConnectStopped() {}
          public void serverAdded(Server server) {
        	  SwingUtilities.invokeLater(new Runnable() {
        		   public void run() {
        			   setServerListCount();
        		   }
        	  });       	   
		  }
          public void serverListCleared() {
        	  SwingUtilities.invokeLater(new Runnable() {
       		      public void run() {
       			      setServerListCount();
       		      } 
       	       });
		  }
          public void serverRemoved(Server server) {
        	  SwingUtilities.invokeLater(new Runnable() {
       		      public void run() {
       			       setServerListCount();
       		      }
       	      });
		  }
		  public void autoConnectFailed() {
			
		  }
		  public void connected(Server server) {
			
		  }
		  public void disconnected(Server server) {

		  }
		  public void isConnecting(Server server) {

		  }
		  public void serverConnectingFailed(Server server, Throwable cause) {

		  }
		  public void serverMessage(Server server, String message) {

		  }
	   });
	   
	   serverListScrollPane.setViewportView(serverListTable);
	   splitPane.setTopComponent(serverListScrollPane);
	   
	   //Set up the border for myInfoScrollPane
	  /* TitledBorder myInfoScrollPaneBorder = 
		   javax.swing.BorderFactory.createTitledBorder(
				   border, 
				   "My info", 
				   javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				   javax.swing.border.TitledBorder.DEFAULT_POSITION, 
				   titledBorderTextFont, 
				   titledBorderTextColor);
	   myInfoScrollPane.setBorder(myInfoScrollPaneBorder);
	   */
	   myInfoScrollPane.setViewportView(myInfoTable);
	   splitPane2.setBottomComponent(myInfoScrollPane);
	   serverMessagesScrollPane.setViewportView(serverMessages);
	   
	   //Set up the border for serverMessagesScrollPane
	   TitledBorder serverMessagesScrollPaneBorder = 
		   javax.swing.BorderFactory.createTitledBorder(
				   border, 
				   "Server messages", 
				   javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				   javax.swing.border.TitledBorder.DEFAULT_POSITION, 
				   titledBorderTextFont, 
				   titledBorderTextColor);
	   //TitledBorder serverMessagesScrollPaneBorder  = javax.swing.BorderFactory.createTitledBorder("Messages");
	   serverMessagesScrollPane.setBorder(serverMessagesScrollPaneBorder);
	   splitPane2.setTopComponent(serverMessagesScrollPane);
	   
	   serverListScrollPane.setPreferredSize(new Dimension(300,200));
	   serverMessagesScrollPane.setPreferredSize(new Dimension(200,300));
	   
	   // Clear server messages popup menu
	   final JPopupMenu popup_menu = new JPopupMenu();
	   JMenuItem clear_menu_item = new JMenuItem("Clear");
	   clear_menu_item.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent event) {
			   serverMessages.setText("");
		   }
	   });
	   popup_menu.add(clear_menu_item);
		
		class MousePopupListener extends MouseAdapter {
		    public void mousePressed(MouseEvent e) {
		      checkPopup(e);
		    }

		    public void mouseClicked(MouseEvent e) {
		      checkPopup(e);
		    }

		    public void mouseReleased(MouseEvent e) {
		      checkPopup(e);
		    }

		    private void checkPopup(MouseEvent e) {
		      if (e.isPopupTrigger()) {
		        popup_menu.show(e.getComponent(), e.getX(), e.getY());
		      }
		    }
		}
	   serverMessages.addMouseListener(new MousePopupListener());
	   setServerListCount();
   }
   
   private void setServerListCount() {
	   String server_count = "Servers(" + _server_manager.getServersCount() + ")";
	   serverListScrollPaneBorder.setTitle(server_count);
	   serverListScrollPane.repaint();
   }
   
   public ServerListTable getServerListTable() {
		return serverListTable;
   }

   public void setServerListTable(ServerListTable serverListTable) {
		this.serverListTable = serverListTable;
   }

   public Info getMyInfoTable() {
		return myInfoTable;
   }

   public void setMyInfoTable(Info myInfoTable) {
		this.myInfoTable = myInfoTable;
   }
   
   public void setServerMessage(String serverMessage) {
	   serverMessages.setText(serverMessage);
   }
   
   public void clearServerMessage() {
	   serverMessages.setText("");
   }
   
}