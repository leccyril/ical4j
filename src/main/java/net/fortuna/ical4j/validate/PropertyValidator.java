/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.util.List;

/**
 * $Id$ [15-May-2004]
 *
 * Defines methods for validating properties and property lists.
 *
 * @author Ben Fortuna
 */
public final class PropertyValidator implements Validator<Property>, ContentValidator<Parameter> {

    private final List<ValidationRule<Property>> rules;

    public PropertyValidator(List<ValidationRule<Property>> rules) {
        this.rules = rules;
    }

    @Override
    public void validate(Property target) throws ValidationException {
        for (ValidationRule<Property> rule : rules) {
            boolean warnOnly = CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
                    && rule.isRelaxedModeSupported();

            if (rule.getPredicate().test(target)) {
                switch (rule.getType()) {
                    case None:
                        rule.getInstances().forEach(s -> assertNone(s, target.getParameters().getAll(), warnOnly));
                        break;
                    case One:
                        rule.getInstances().forEach(s -> assertOne(s, target.getParameters().getAll(), warnOnly));
                        break;
                    case OneOrLess:
                        rule.getInstances().forEach(s -> assertOneOrLess(s, target.getParameters().getAll(), warnOnly));
                        break;
                }
            }
        }
    }
}
