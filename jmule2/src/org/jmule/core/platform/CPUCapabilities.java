/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.platform;

/**
 * Created on Aug 30, 2009
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2009/08/31 17:26:28 $
 */
public class CPUCapabilities {

	private String number;
	private String vendorId;
	private String family;
	private String model;
	private String modelName;
	private String stepping;
	private String MHz;
	private String cacheSize;
	private String physicalId;
	private String siblings;
	private String coreId;
	private String nrOfCores;
	private String apicid;
	private String initialApicid;
	private boolean fdivBug;
	private boolean hltBug;
	private boolean f00fBug;
	private boolean comaBug;
	private boolean fpu;
	private boolean fpuException;
	private String cpuidLevel;
	private boolean wp;
	private String[] flags;
	private String bogomips;
	private String clflushSize;
    private String powerManagerment;
    
    CPUCapabilities() {
    	
    }
    
	public String getNumber() {
		return number;
	}
	void setNumber(String number) {
		this.number = number;
	}
	public String getVendorId() {
		return vendorId;
	}
	void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	public String getFamily() {
		return family;
	}
	void setFamily(String family) {
		this.family = family;
	}
	public String getModel() {
		return model;
	}
	void setModel(String model) {
		this.model = model;
	}
	public String getModelName() {
		return modelName;
	}
	void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getStepping() {
		return stepping;
	}
	void setStepping(String stepping) {
		this.stepping = stepping;
	}
	public String getMHz() {
		return MHz;
	}
	void setMHz(String mHz) {
		MHz = mHz;
	}
	public String getCacheSize() {
		return cacheSize;
	}
	void setCacheSize(String cacheSize) {
		this.cacheSize = cacheSize;
	}
	public String getPhysicalId() {
		return physicalId;
	}
	void setPhysicalId(String physicalId) {
		this.physicalId = physicalId;
	}
	public String getSiblings() {
		return siblings;
	}
	void setSiblings(String siblings) {
		this.siblings = siblings;
	}
	public String getCoreId() {
		return coreId;
	}
	void setCoreId(String coreId) {
		this.coreId = coreId;
	}
	public String getNrOfCores() {
		return nrOfCores;
	}
	void setNrOfCores(String nrOfCores) {
		this.nrOfCores = nrOfCores;
	}
	public String getApicid() {
		return apicid;
	}
	void setApicid(String apicid) {
		this.apicid = apicid;
	}
	public String getInitialApicid() {
		return initialApicid;
	}
	void setInitialApicid(String initialApicid) {
		this.initialApicid = initialApicid;
	}
	public boolean isFdivBug() {
		return fdivBug;
	}
	void setFdivBug(boolean fdivBug) {
		this.fdivBug = fdivBug;
	}
	public boolean isHltBug() {
		return hltBug;
	}
	void setHltBug(boolean hltBug) {
		this.hltBug = hltBug;
	}
	public boolean isF00fBug() {
		return f00fBug;
	}
	void setF00fBug(boolean f00fBug) {
		this.f00fBug = f00fBug;
	}
	public boolean isComaBug() {
		return comaBug;
	}
	void setComaBug(boolean comaBug) {
		this.comaBug = comaBug;
	}
	public boolean isFpu() {
		return fpu;
	}
	void setFpu(boolean fpu) {
		this.fpu = fpu;
	}
	public boolean isFpuException() {
		return fpuException;
	}
	void setFpuException(boolean fpuException) {
		this.fpuException = fpuException;
	}
	public String getCpuidLevel() {
		return cpuidLevel;
	}
	void setCpuidLevel(String cpuidLevel) {
		this.cpuidLevel = cpuidLevel;
	}
	public boolean isWp() {
		return wp;
	}
	void setWp(boolean wp) {
		this.wp = wp;
	}
	public String[] getFlags() {
		return flags;
	}
	void setFlags(String[] flags) {
		this.flags = flags;
	}
	public String getBogomips() {
		return bogomips;
	}
	void setBogomips(String bogomips) {
		this.bogomips = bogomips;
	}
	public String getClflushSize() {
		return clflushSize;
	}
	void setClflushSize(String clflushSize) {
		this.clflushSize = clflushSize;
	}
	public String getPowerManagerment() {
		return powerManagerment;
	}
	void setPowerManagerment(String powerManagerment) {
		this.powerManagerment = powerManagerment;
	}   
	
}
