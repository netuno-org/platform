/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netuno.tritao.openapi;

import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;
import org.netuno.psamata.Values;

import java.util.*;

/**
 * Validation Problem Handler
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ValidationProblemHandler implements ProblemHandler {
    private final List<Problem> problems = new ArrayList<>();
    private final JsonValidationService jsonValidationService;
    private String problemsPrinter = "";

    protected ValidationProblemHandler(JsonValidationService jsonValidationService) {
        this.jsonValidationService = jsonValidationService;
    }

    @Override
    public void handleProblems(List<Problem> list) {
        problems.addAll(list);
    }

    public boolean hasNoProblem() {
        return problems.isEmpty();
    }

    public Values getProblems() {
        Values list = Values.newList();
        for (Problem p : problems) {
            render(p, list);
        }
        return list;
    }

    public String toPrint() {
        problemsPrinter = "";
        ProblemHandler handler = jsonValidationService.createProblemPrinter(this::problemPrint);
        handler.handleProblems(problems);
        return problemsPrinter;
    }

    private void problemPrint(String problem) {
        if (!problemsPrinter.isEmpty()) {
            problemsPrinter += "\n";
        }
        problemsPrinter += "#     " + problem;
    }

    private void render(Problem problem, Values list) {
        if (!problem.hasBranches()) {
            Values data = new Values(problem.parametersAsMap());
            data.set("message", problem.getMessage());
            list.add(data);
        } else {
            this.renderProblem(problem, list);
        }
    }

    private void renderProblem(Problem problem, Values list) {
        int numOfBranches = problem.countBranches();
        if (numOfBranches > 1) {
            this.renderBranchingProblem(problem, list);
        } else if (numOfBranches == 1) {
            this.renderFirstBranchOnly(problem, list);
        } else {
            this.renderSimpleProblem(problem, list);
        }
    }

    private void renderBranchingProblem(Problem problem, Values list) {
        int numOfBranches = problem.countBranches();
        Values data = Values.newMap();
        data.set("message", problem.getMessage());
        Values branches = Values.newList();
        for(int i = 0; i < numOfBranches; ++i) {
            Values branchData = new Values(problem.parametersAsMap());
            branchData.set("index", i + 1);
            branchData.set("message", problem.getMessage());
            Values branchList = Values.newList();
            for (Problem value : problem.getBranch(i)) {
                this.renderProblem(value, branchList);
            }
            branchData.set("problems", branchList);
        }
        data.set("branches", branches);
        list.add(data);
    }

    private void renderFirstBranchOnly(Problem problem, Values list) {
        for (Problem value : problem.getBranch(0)) {
            this.renderProblem(value, list);
        }
    }

    private void renderSimpleProblem(Problem problem, Values list) {
        Values data = new Values(problem.parametersAsMap());
        data.set("message", problem.getMessage());
        list.add(data);
    }
}
