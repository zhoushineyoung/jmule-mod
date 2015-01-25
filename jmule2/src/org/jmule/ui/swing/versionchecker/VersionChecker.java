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
package org.jmule.ui.swing.versionchecker;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.jmule.core.JMConstants;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swing.BrowserLauncher;
import org.jmule.ui.swing.SwingPreferences;
import org.jmule.updater.JMUpdater;

/**
 *
 * Created on Oct 11, 2008
 * @author javajox
 * @version $Revision: 1.3 $
 * Last changed by $Author: javajox $ on $Date: 2010/01/13 16:05:51 $
 */
public class VersionChecker extends JDialog {

	private JLabel jmule_version_label = new JLabel("JMule version");
	private JLabel available_version_label = new JLabel("Available version");
	private JLabel last_update_label = new JLabel("Last update");
	private JScrollPane changelog_scroll_panel = new JScrollPane();
	private JLabel available_version_value = new JLabel();
	private JCheckBox check_for_update_checkbox = new JCheckBox("Check for updates at startup");
	private JButton ok_button = new JButton("OK");
	private JLabel last_update_value = new JLabel();
	private JLabel jmule_version_value = new JLabel(JMConstants.JMULE_FULL_NAME);
	private JEditorPane changelog_editor_panel = new JEditorPane();
	private JLabel download_new_version_label = new JLabel();
	private JFrame parent;
	
	private JMUpdater jmule_updater = JMUpdater.getInstance();
	private SwingPreferences _pref = SwingPreferences.getSingleton();
	
	private Font dialog_font = new java.awt.Font("Dialog", 1, 13);
	
	public VersionChecker(JFrame parent) {
		super(parent, "Version checker", true);
		try {
			jmule_updater.checkForUpdates();
		}catch(Throwable t) {
			JOptionPane.showMessageDialog(this, "An error occured", "Error",JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
		}
		this.parent = parent;
		init();
		if(jmule_updater.isNewVersionAvailable()) {
			available_version_value.setForeground(new Color(0x24bb00));
			jmule_version_value.setForeground(Color.RED);
			available_version_value.setText(jmule_updater.getVersion());
			download_new_version_label.setText("Download new version");
			changelog_editor_panel.setEnabled(true);
			changelog_editor_panel.setFont(new Font("Courir", Font.PLAIN, 12));
			changelog_editor_panel.setText(jmule_updater.getChangeLog());
			
			download_new_version_label.addMouseListener(new MouseAdapter() {
				 public void mouseClicked(MouseEvent event) {
					 BrowserLauncher.openURL(JMConstants.JMULE_DOWNLOAD_PAGE);
				 }
				 public void mouseEntered(MouseEvent event) {
					 download_new_version_label.setForeground(Color.BLUE);
					 setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
				 }
				 public void mouseExited(MouseEvent event) {
					 download_new_version_label.setForeground(Color.BLACK);
					 setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
				 }
			});
			
		} else {
			download_new_version_label.setText("No new version available");
		}
		long check_time = jmule_updater.getLastUpdateTime();
		if (check_time != 0) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(check_time);
			String upate_date = format(calendar.get(Calendar.DAY_OF_MONTH)) + "." +format(calendar.get(Calendar.MONTH) + 1) +"."+ format(calendar.get(Calendar.YEAR));
			upate_date += "  "+format(calendar.get(Calendar.HOUR_OF_DAY)) + ":"+format(calendar.get(Calendar.MINUTE))+":"+format(calendar.get(Calendar.SECOND));
			last_update_value.setText(upate_date);
		}
	}
	
	private void init() {
		check_for_update_checkbox.setSelected(_pref.isCheckForUpdatesAtStartup());
		GridBagLayout thisLayout = new GridBagLayout();
		thisLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1};
		thisLayout.rowHeights = new int[] {17, 20, 20, 20, 29, 175, 7};
		thisLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1, 0.0};
		thisLayout.columnWidths = new int[] {7, 158, 18, 7, 7};
		getContentPane().setLayout(thisLayout);
		getContentPane().add(jmule_version_label, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(available_version_label, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(last_update_label, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(download_new_version_label, new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(changelog_scroll_panel, new GridBagConstraints(1, 5, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 0, 0, 0), 0, 0));
		changelog_scroll_panel.setBorder(BorderFactory.createTitledBorder("Changelog"));
		changelog_scroll_panel.setViewportView(changelog_editor_panel);
        getContentPane().add(jmule_version_value, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	    getContentPane().add(available_version_value, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(last_update_value, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(check_for_update_checkbox, new GridBagConstraints(1, 6, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(ok_button, new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 50, 0, 0), 0, 0));
		changelog_editor_panel.setEditable(false);
		changelog_editor_panel.setEnabled(false);
		download_new_version_label.setHorizontalAlignment(SwingConstants.CENTER);
		jmule_version_value.setFont(dialog_font);
		jmule_version_value.setHorizontalAlignment(SwingConstants.LEFT);
		available_version_value.setFont(dialog_font);
		available_version_value.setHorizontalAlignment(SwingConstants.LEFT);
		last_update_value.setFont(dialog_font);
		last_update_value.setHorizontalAlignment(SwingConstants.LEFT);
		ok_button.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent event) {	
		      _pref.setCheckForUpdatesAtStartup(check_for_update_checkbox.isSelected());
	          setVisible(false);		
		   }
		});
		this.setSize(380, 357);
	}
	
	private String format(int value) {
		return value<10 ? "0"+value : value+"";
	}	
}
