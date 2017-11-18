/**
 * Copyright 2017 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.clava.ast.omp.clauses;

import static pt.up.fe.specs.clava.ast.omp.clauses.OmpClauseKind.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pt.up.fe.specs.clava.ast.omp.OmpPragma;
import pt.up.fe.specs.clava.ast.omp.clauses.OmpProcBindClause.ProcBindKind;
import pt.up.fe.specs.clava.ast.omp.clauses.OmpReductionClause.ReductionKind;
import pt.up.fe.specs.util.SpecsCollections;

public class OmpClauses {

    private final OmpPragma ompPragma;

    public OmpClauses(OmpPragma ompPragma) {
        this.ompPragma = ompPragma;
    }

    // HELPER METHODS

    private List<String> getListClauseVariables(OmpClauseKind kind) {
        return SpecsCollections.cast(ompPragma.getClause(kind), OmpListClause.class).stream()
                .flatMap(clauseList -> clauseList.getVariables().stream())
                .collect(Collectors.toList());
    }

    // GETTERS AND SETTERS

    public Optional<ProcBindKind> getProcBind() {
        return ompPragma.getClause(OmpClauseKind.PROC_BIND).stream()
                .findFirst()
                .map(OmpProcBindClause.class::cast)
                .map(OmpProcBindClause::getProcBindKind);
    }

    public void setProcBind(ProcBindKind kind) {
        ompPragma.setClause(new OmpProcBindClause(kind));
    }

    public Optional<String> getNumThreads() {
        return ompPragma.getClause(OmpClauseKind.NUM_THREADS).stream()
                .findFirst()
                .map(OmpIntegerExpressionClause.class::cast)
                .map(OmpIntegerExpressionClause::getExpression);
    }

    public void setNumThreads(String expression) {
        ompPragma.setClause(OmpClauseFactory.numThreads(expression));
    }

    public List<String> getPrivate() {
        return getListClauseVariables(PRIVATE);
    }

    public void setPrivate(List<String> variables) {
        ompPragma.setClause(new OmpListClause(PRIVATE, variables));
    }

    public List<String> getReduction(String kindString) {
        ReductionKind kind = ReductionKind.getHelper()
                .valueOfTry(kindString.toLowerCase())
                .orElseThrow(() -> new RuntimeException(
                        "'" + kindString + "' is not a valid reduction. Available reductions: "
                                + ReductionKind.getHelper().getAvailableOptions()));
        // ReductionKind kind = ReductionKind.getHelper().valueOfTry(kindString.toLowerCase()).orElse(null);
        // if (kind == null) {
        // SpecsLogs.msgInfo("String '" + kindString + "' is not a valid reduction. Available reductions: "
        // + ReductionKind.getHelper().getAvailableOptions());
        // return Collections.emptyList();
        // }

        return getReduction(kind);
    }

    public List<String> getReduction(ReductionKind kind) {

        return ompPragma.getClause(REDUCTION).stream().map(OmpReductionClause.class::cast)
                .filter(reduction -> reduction.getReductionKind() == kind)
                .flatMap(reduction -> reduction.getVariables().stream())
                .collect(Collectors.toList());
    }

    public void setReductionImpl(ReductionKind kind, List<String> variables) {

        // Get all reduction clauses
        List<OmpClause> reductionClause = ompPragma.getClause(REDUCTION);

        List<OmpClause> newReductionClause = new ArrayList<>();

        // Store all reductions that are not of the kind to set
        reductionClause.stream()
                .map(OmpReductionClause.class::cast)
                .filter(reduction -> reduction.getReductionKind() != kind)
                .forEach(newReductionClause::add);

        // Add new reduction
        newReductionClause.add(new OmpReductionClause(kind, variables));

        ompPragma.setClause(newReductionClause);
    }

    // public List<OmpListClause> getListClause(OmpClauseKind kind) {
    // return SpecsCollections.cast(ompPragma.getClause(kind), OmpListClause.class);
    // }

    // private static <K> Optional<K> returnFirst(List<K> list) {
    // if (list.isEmpty()) {
    // return Optional.empty();
    // }
    //
    // return Optional.of(list.get(0));
    // }

}
