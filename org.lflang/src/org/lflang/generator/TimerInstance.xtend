/** Instance of a timer. */

/*************
Copyright (c) 2019, The University of California at Berkeley.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
***************/

package org.lflang.generator

import org.eclipse.xtend.lib.annotations.Accessors
import org.lflang.TimeValue
import org.lflang.lf.TimeUnit
import org.lflang.lf.Timer

import static extension org.lflang.ASTUtils.*

/**
 * Instance of a timer.
 * 
 * @author{Marten Lohstroh <marten@berkeley.edu>}
 * @author{Edward A. Lee <eal@berkeley.edu>}
 */
class TimerInstance extends TriggerInstance<Timer> {
    
    /** The global default for offset. */
    public static val DEFAULT_OFFSET = new TimeValue(0, TimeUnit.NONE)
    /** The global default for period. */
    public static val DEFAULT_PERIOD = new TimeValue(0, TimeUnit.NONE)
    
    @Accessors(PUBLIC_GETTER)
	protected TimeValue offset = DEFAULT_OFFSET
    @Accessors(PUBLIC_GETTER)
    protected TimeValue period = DEFAULT_PERIOD
	
	/**
	 * Create a new timer instance.
	 * If the definition is null, then this is a startup timer.
	 * @param definition The AST definition, or null for startup.
	 * @param parent The parent reactor.
	 */
	new(Timer definition, ReactorInstance parent) {
		super(definition, parent)
        if (parent === null) {
            throw new InvalidSourceException('Cannot create an TimerInstance with no parent.')
        }
        if (definition !== null) {
            if (definition.offset !== null) {
                if (definition.offset.parameter !== null) {
                    val parm = definition.offset.parameter
                    this.offset = parent.lookupParameterInstance(parm).init.get(0).getTimeValue
                } else {
                    this.offset = definition.offset.timeValue
                }
            }
            if (definition.period !== null) {
                if (definition.period.parameter !== null) {
                    val parm = definition.period.parameter
                    this.period = parent.lookupParameterInstance(parm).init.get(0).getTimeValue
                } else {
                    this.period = definition.period.timeValue
                }
            }
        }
    }
}