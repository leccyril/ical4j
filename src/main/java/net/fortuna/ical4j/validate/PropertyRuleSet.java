/*
 *  Copyright (c) 2022, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PropertyRuleSet<T extends Property> extends AbstractValidationRuleSet<T> {

    @SafeVarargs
    public PropertyRuleSet(ValidationRule<T>... rules) {
        super(rules);
    }

    @Override
    public List<ValidationEntry> apply(String context, T target) {
        List<ValidationEntry> results = new ArrayList<>();
        for (ValidationRule<T> rule: rules) {
            List<String> matches = Collections.emptyList();
            if (rule.getPredicate().test(target)) {
                int total = rule.getInstances().stream().mapToInt(s -> target.getParameters(s).size()).sum();
                switch (rule.getType()) {
                    case None:
                        matches = matches(rule.getInstances(), s -> target.getParameter(s) != null);
                        break;
                    case One:
                        matches = matches(rule.getInstances(), s -> target.getParameters(s).size() != 1);
                        break;
                    case OneOrLess:
                        matches = matches(rule.getInstances(), s -> target.getParameters(s).size() > 1);
                        break;
                    case OneOrMore:
                        matches = matches(rule.getInstances(), s -> target.getParameters(s).size() < 1);
                        break;
                    case OneExclusive:
                        if (rule.getInstances().stream().anyMatch(s -> target.getParameters(s).size() > 0
                                && target.getParameters(s).size() != total)) {
                            matches = rule.getInstances();
                        }
                        break;
                    case AllOrNone:
                        if (total > 0 && total != rule.getInstances().size()) {
                            results.add(new ValidationEntry(rule, target.getName()));
                        }
                        break;
                    case ValueMatch:
                        matches = matches(rule.getInstances(), s -> !target.getValue().matches(s));
                        break;
                }
                if (!matches.isEmpty()) {
                    results.add(new ValidationEntry(rule, target.getName(), matches.toArray(new String[0])));
                }
            }
        }
        return results;
    }
}
