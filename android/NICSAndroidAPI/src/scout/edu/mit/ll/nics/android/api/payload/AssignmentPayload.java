/*|~^~|Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 |~^~|All rights reserved.
 |~^~|
 |~^~|Redistribution and use in source and binary forms, with or without
 |~^~|modification, are permitted provided that the following conditions are met:
 |~^~|
 |~^~|-1. Redistributions of source code must retain the above copyright notice, this
 |~^~|ist of conditions and the following disclaimer.
 |~^~|
 |~^~|-2. Redistributions in binary form must reproduce the above copyright notice,
 |~^~|this list of conditions and the following disclaimer in the documentation
 |~^~|and/or other materials provided with the distribution.
 |~^~|
 |~^~|-3. Neither the name of the copyright holder nor the names of its contributors
 |~^~|may be used to endorse or promote products derived from this software without
 |~^~|specific prior written permission.
 |~^~|
 |~^~|THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 |~^~|AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 |~^~|IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 |~^~|DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 |~^~|FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 |~^~|DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 |~^~|SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 |~^~|CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 |~^~|OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 |~^~|OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\*/
/**
 *
 */
package scout.edu.mit.ll.nics.android.api.payload;

import java.util.ArrayList;
import java.util.List;

import scout.edu.mit.ll.nics.android.api.data.OperationalPeriod;
import scout.edu.mit.ll.nics.android.api.data.OperationalUnit;

/**
 * @author Glenn L. Primmer
 *
 */
public class AssignmentPayload {
    private List<ResourcePayload> phiResourcesAssigns;
    private List<TaskPayload>  phiTaskAssigns;
    private OperationalPeriod phiOperationalPeriod;
    private OperationalUnit   phiUnit;

    /**
     * Lazy constructor.
     */
    public AssignmentPayload () {
        phiResourcesAssigns = new ArrayList<ResourcePayload>();
        phiTaskAssigns = new ArrayList<TaskPayload>();
        phiOperationalPeriod = new OperationalPeriod();
        phiUnit = new OperationalUnit();
    }

	public List<ResourcePayload> getPhiResourcesAssigns() {
		return phiResourcesAssigns;
	}

	public void setPhiResourcesAssigns(List<ResourcePayload> phiResourcesAssigns) {
		this.phiResourcesAssigns = phiResourcesAssigns;
	}

	public List<TaskPayload> getPhiTaskAssigns() {
		return phiTaskAssigns;
	}

	public void setPhiTaskAssigns(List<TaskPayload> phiTaskAssigns) {
		this.phiTaskAssigns = phiTaskAssigns;
	}

	public OperationalPeriod getPhiOperationalPeriod() {
		return phiOperationalPeriod;
	}

	public void setPhiOperationalPeriod(OperationalPeriod phiOperationalPeriod) {
		this.phiOperationalPeriod = phiOperationalPeriod;
	}

	public OperationalUnit getPhiUnit() {
		return phiUnit;
	}

	public void setPhiUnit(OperationalUnit phiUnit) {
		this.phiUnit = phiUnit;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		
		if(o instanceof AssignmentPayload && o != null) {
			AssignmentPayload otherPayload = (AssignmentPayload)o;
			return phiOperationalPeriod.equals(otherPayload.phiOperationalPeriod) && phiUnit.equals(otherPayload.phiUnit);
		}
		
		return false;
	}
}
