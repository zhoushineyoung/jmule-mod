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
package org.jmule.core.edonkey.utils;

import static org.jmule.core.edonkey.ED2KConstants.ET_COMMENTS;
import static org.jmule.core.edonkey.ED2KConstants.ET_COMPRESSION;
import static org.jmule.core.edonkey.ED2KConstants.ET_EXTENDEDREQUEST;
import static org.jmule.core.edonkey.ED2KConstants.ET_FEATURES;
import static org.jmule.core.edonkey.ED2KConstants.ET_SOURCEEXCHANGE;
import static org.jmule.core.edonkey.ED2KConstants.ET_UDPVER;
import static org.jmule.core.edonkey.ED2KConstants.SRV_TCPFLG_COMPRESSION;
import static org.jmule.core.edonkey.ED2KConstants.SRV_TCPFLG_LARGEFILES;
import static org.jmule.core.edonkey.ED2KConstants.SRV_TCPFLG_NEWTAGS;
import static org.jmule.core.edonkey.ED2KConstants.SRV_TCPFLG_RELATEDSEARCH;
import static org.jmule.core.edonkey.ED2KConstants.SRV_TCPFLG_TCPOBFUSCATION;
import static org.jmule.core.edonkey.ED2KConstants.SRV_TCPFLG_TYPETAGINTEGER;
import static org.jmule.core.edonkey.ED2KConstants.SRV_TCPFLG_UNICODE;
import static org.jmule.core.edonkey.ED2KConstants.SRV_UDPFLG_EXT_GETFILES;
import static org.jmule.core.edonkey.ED2KConstants.SRV_UDPFLG_EXT_GETSOURCES;
import static org.jmule.core.edonkey.ED2KConstants.SRV_UDPFLG_EXT_GETSOURCES2;
import static org.jmule.core.edonkey.ED2KConstants.SRV_UDPFLG_LARGEFILES;
import static org.jmule.core.edonkey.ED2KConstants.SRV_UDPFLG_NEWTAGS;
import static org.jmule.core.edonkey.ED2KConstants.SRV_UDPFLG_TCPOBFUSCATION;
import static org.jmule.core.edonkey.ED2KConstants.SRV_UDPFLG_UDPOBFUSCATION;
import static org.jmule.core.edonkey.ED2KConstants.SRV_UDPFLG_UNICODE;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.AcceptCommentVersion;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.DataCompressionVersion;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.DirectUDPCallback;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.ExtMultiPacket;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.ExtendedRequestsVersion;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.KadVersion;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.ModBit;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.MultiPacket;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.NoViewSharedFiles;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.PeerCache;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.RequestsCryptLayer;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.RequiresCryptLayer;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.SourceExchange1Version;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.SupportsAICH;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.SupportsCaptcha;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.SupportsCryptLayer;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.SupportsFileIdent;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.SupportsLargeFiles;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.SupportsPreview;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.SupportsSecureIdentification;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.SupportsSourceEx2;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.SupportsUnicode;
import static org.jmule.core.edonkey.ED2KConstants.PeerFeatures.UDPVersion;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.jmule.core.edonkey.ED2KConstants.PeerFeatures;
import org.jmule.core.edonkey.ED2KConstants.ServerFeatures;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;

/**
 * Created on Dec 24, 2008
 * @author binary256
 * @version $Revision: 1.8 $
 * Last changed by $Author: binary255 $ on $Date: 2010/08/22 12:47:38 $
 */
public class Utils {

	public static int peerFeatures1ToInt( Map<PeerFeatures, Integer> clientFeatures) {
		int misc_optins1 = 0;
		
		if (clientFeatures.containsKey(SupportsAICH))
			misc_optins1 |= (byte)(clientFeatures.get(SupportsAICH) << 29);
		if (clientFeatures.containsKey(SupportsUnicode))
			misc_optins1 |= (int)(clientFeatures.get(SupportsUnicode) << 28);
		if (clientFeatures.containsKey(UDPVersion))
			misc_optins1 |= (int)(clientFeatures.get(UDPVersion) << 24);
		if (clientFeatures.containsKey(DataCompressionVersion))
			misc_optins1 |= (int)(clientFeatures.get(DataCompressionVersion) << 20);
		if (clientFeatures.containsKey(SupportsSecureIdentification))
			misc_optins1 |= (int)(clientFeatures.get(SupportsSecureIdentification) << 16);
		if (clientFeatures.containsKey(SourceExchange1Version))
			misc_optins1 |= (int)(clientFeatures.get(SourceExchange1Version) << 12);
		if (clientFeatures.containsKey(ExtendedRequestsVersion))
			misc_optins1 |= (int)(clientFeatures.get(ExtendedRequestsVersion) << 8);
		if (clientFeatures.containsKey(AcceptCommentVersion))
			misc_optins1 |= (int)(clientFeatures.get(AcceptCommentVersion) << 4);
		if (clientFeatures.containsKey(PeerCache))
			misc_optins1 |= (byte)(clientFeatures.get(PeerCache) << 3);
		if (clientFeatures.containsKey(NoViewSharedFiles))
			misc_optins1 |= (byte)(clientFeatures.get(NoViewSharedFiles) << 2);
		if (clientFeatures.containsKey(MultiPacket))
			misc_optins1 |= (byte)(clientFeatures.get(MultiPacket) << 1);
		if (clientFeatures.containsKey(SupportsPreview))
			misc_optins1 |= (byte)(clientFeatures.get(SupportsPreview) << 0);
		
		return misc_optins1;
	}
	
	public static int peerFeatures2ToInt(Map<PeerFeatures, Integer> clientFeatures) {
		int misc_optins2 = 0;
		if (clientFeatures.containsKey(SupportsFileIdent))
			misc_optins2 |= (clientFeatures.get(SupportsFileIdent) << 13);
		if (clientFeatures.containsKey(DirectUDPCallback))
			misc_optins2 |= (clientFeatures.get(DirectUDPCallback) << 12);
		if (clientFeatures.containsKey(SupportsCaptcha))
			misc_optins2 |= (clientFeatures.get(SupportsCaptcha) << 11);
		if (clientFeatures.containsKey(SupportsSourceEx2))
			misc_optins2 |= (clientFeatures.get(SupportsSourceEx2) << 10);
		if (clientFeatures.containsKey(RequiresCryptLayer))
			misc_optins2 |= (clientFeatures.get(RequiresCryptLayer) << 9);
		if (clientFeatures.containsKey(RequestsCryptLayer))
			misc_optins2 |= (clientFeatures.get(RequestsCryptLayer) << 8);
		if (clientFeatures.containsKey(SupportsCryptLayer))
			misc_optins2 |= (clientFeatures.get(SupportsCryptLayer) << 7);
		if (clientFeatures.containsKey(ModBit))
			misc_optins2 |= (clientFeatures.get(ModBit) << 6);
		if (clientFeatures.containsKey(ExtMultiPacket))
			misc_optins2 |= (clientFeatures.get(ExtMultiPacket) << 5);
		if (clientFeatures.containsKey(SupportsLargeFiles))
			misc_optins2 |= (clientFeatures.get(SupportsLargeFiles) << 4);
		if (clientFeatures.containsKey(KadVersion))
			misc_optins2 |= (clientFeatures.get(KadVersion) << 0);
		
		return misc_optins2;
	}
	
	public static Map<PeerFeatures,Integer> scanTCPPeerFeatures1(int rawData) {
		Map<PeerFeatures,Integer> result = new Hashtable<PeerFeatures,Integer>();
		
		result.put(SupportsAICH, (rawData >> 29) & 0x07);
		result.put(SupportsUnicode, (rawData >> 28) & 0x01);
		result.put(UDPVersion, (rawData >> 24) & 0x0f);
		result.put(DataCompressionVersion, (rawData >> 20) & 0x0f);
		result.put(SupportsSecureIdentification, (rawData >> 16) & 0x0f);
		result.put(SourceExchange1Version, (rawData >> 12) & 0x0f);
		result.put(ExtendedRequestsVersion,(rawData >>  8) & 0x0f);
		result.put(AcceptCommentVersion, (rawData >>  4) & 0x0f);
		result.put(PeerCache, (rawData >>  3) & 0x01);
		result.put(NoViewSharedFiles, (rawData >>  2) & 0x01);
		result.put(MultiPacket, (rawData >>  1) & 0x01);
		result.put(SupportsPreview, (rawData >>  0) & 0x01);
		
		return result;
	}
	
	public static Map<PeerFeatures,Integer> scanTCPPeerFeatures2(int rawData) {
		Map<PeerFeatures,Integer> result = new Hashtable<PeerFeatures,Integer>();
		result.put(SupportsFileIdent, (rawData >> 13) & 0x01);
		result.put(DirectUDPCallback, (rawData >> 12) & 0x01);
		result.put(SupportsCaptcha, (rawData >> 11) & 0x01);
		result.put(SupportsSourceEx2, (rawData >> 10) & 0x01);
		result.put(RequiresCryptLayer, (rawData >> 9) & 0x01);
		result.put(RequestsCryptLayer, (rawData >> 8) & 0x01);
		result.put(SupportsCryptLayer, (rawData >> 7) & 0x01);
		result.put(ModBit, (rawData >> 6) & 0x01);
		result.put(ExtMultiPacket, (rawData >> 5) & 0x01);
		result.put(SupportsLargeFiles, (rawData >> 4) & 0x01);
		result.put(KadVersion, (rawData >> 0) & 0x01);
		
		return result;
	}
	
	public static Map<PeerFeatures, Integer> scanTagListPeerFeatures(TagList tagList) {
		Map<PeerFeatures,Integer> result = new Hashtable<PeerFeatures,Integer>();
		Tag tag;
		
		tag = tagList.getTag(ET_COMPRESSION);
		if (tag != null)
			result.put(DataCompressionVersion, (Integer)tag.getValue());
		
		tag = tagList.getTag(ET_UDPVER);
		if (tag != null)
			result.put(UDPVersion, (Integer)tag.getValue());
		
		tag = tagList.getTag(ET_SOURCEEXCHANGE);
		if (tag != null)
			result.put(SourceExchange1Version, (Integer)tag.getValue());
		
		tag = tagList.getTag(ET_COMMENTS);
		if (tag != null)
			result.put(AcceptCommentVersion, (Integer)tag.getValue());
		
		tag = tagList.getTag(ET_EXTENDEDREQUEST);
		if (tag != null)
			result.put(ExtendedRequestsVersion, (Integer)tag.getValue());
		
		tag = tagList.getTag(ET_FEATURES);
		if (tag != null)
			result.put(SupportsPreview, (Integer)tag.getValue());
		
		return result;
	}
	
	public static int serverFeaturesToInt(Set<ServerFeatures> features) {
		int result = 0;
		//TCP
		if (features.contains(ServerFeatures.Compression))
			result |= SRV_TCPFLG_COMPRESSION;
		if (features.contains(ServerFeatures.NewTags))
			result |= SRV_TCPFLG_NEWTAGS;
		if (features.contains(ServerFeatures.Unicode))
			result |= SRV_TCPFLG_UNICODE;
		if (features.contains(ServerFeatures.RelatedSearch))
			result |= SRV_TCPFLG_RELATEDSEARCH;
		if (features.contains(ServerFeatures.TypeTagInteger))
			result |= SRV_TCPFLG_TYPETAGINTEGER;
		if (features.contains(ServerFeatures.LargeFiles))
			result |= SRV_TCPFLG_LARGEFILES;
		if (features.contains(ServerFeatures.TCPObfusication))
			result |= SRV_TCPFLG_TCPOBFUSCATION;
		//UDP
		if (features.contains(ServerFeatures.GetSources))
			result |= SRV_UDPFLG_EXT_GETSOURCES;
		if (features.contains(ServerFeatures.GetFiles))
			result |= SRV_UDPFLG_EXT_GETFILES;
		if (features.contains(ServerFeatures.NewTags))
			result |= SRV_UDPFLG_NEWTAGS;
		if (features.contains(ServerFeatures.Unicode))
			result |= SRV_UDPFLG_UNICODE;
		if (features.contains(ServerFeatures.GetSources2))
			result |= SRV_UDPFLG_EXT_GETSOURCES2;
		if (features.contains(ServerFeatures.LargeFiles))
			result |= SRV_UDPFLG_LARGEFILES;
		if (features.contains(ServerFeatures.UDPObfusication))
			result |= SRV_UDPFLG_UDPOBFUSCATION;
		if (features.contains(ServerFeatures.TCPObfusication))
			result |= SRV_UDPFLG_TCPOBFUSCATION;
		return result;
	}
	
	public static Set<ServerFeatures> scanTCPServerFeatures(int serverFeatures) {
		Set<ServerFeatures> result = new HashSet<ServerFeatures>();
		
		if ((serverFeatures & SRV_TCPFLG_COMPRESSION) != 0) 
			result.add(ServerFeatures.Compression);

		if ((serverFeatures & SRV_TCPFLG_NEWTAGS) != 0) 
			result.add(ServerFeatures.NewTags);
		
		if ((serverFeatures & SRV_TCPFLG_UNICODE) != 0) 
			result.add(ServerFeatures.Unicode);
		
		if ((serverFeatures & SRV_TCPFLG_RELATEDSEARCH) != 0) 
			result.add(ServerFeatures.RelatedSearch);
		
		if ((serverFeatures & SRV_TCPFLG_TYPETAGINTEGER) != 0) 
			result.add(ServerFeatures.TypeTagInteger);
		
		if ((serverFeatures & SRV_TCPFLG_LARGEFILES) != 0) 
			result.add(ServerFeatures.LargeFiles);
		
		if ((serverFeatures & SRV_TCPFLG_TCPOBFUSCATION) != 0) 
			result.add(ServerFeatures.TCPObfusication);
		
		return result;
	}
	
	public static Set<ServerFeatures> scanUDPServerFeatures(int serverUDPFeatures) {
		Set<ServerFeatures> result = new HashSet<ServerFeatures>();
		
		if ((serverUDPFeatures & SRV_UDPFLG_EXT_GETSOURCES) != 0) 
			result.add(ServerFeatures.GetSources);
		
		if ((serverUDPFeatures & SRV_UDPFLG_EXT_GETFILES) != 0) 
			result.add(ServerFeatures.GetFiles);
		
		if ((serverUDPFeatures & SRV_UDPFLG_NEWTAGS) != 0) 
			result.add(ServerFeatures.NewTags);
		
		if ((serverUDPFeatures & SRV_UDPFLG_UNICODE) != 0) 
			result.add(ServerFeatures.Unicode);
		
		if ((serverUDPFeatures & SRV_UDPFLG_EXT_GETSOURCES2) != 0) 
			result.add(ServerFeatures.GetSources2);
		
		if ((serverUDPFeatures & SRV_UDPFLG_LARGEFILES) != 0) 
			result.add(ServerFeatures.LargeFiles);
		
		if ((serverUDPFeatures & SRV_UDPFLG_UDPOBFUSCATION) != 0) 
			result.add(ServerFeatures.UDPObfusication);
		
		if ((serverUDPFeatures & SRV_UDPFLG_TCPOBFUSCATION) != 0) 
			result.add(ServerFeatures.TCPObfusication);
		
		return result;
	}
	
}
