/**
 * Copyright 2013 SuikaSoft.
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

package pt.up.fe.specs.clava.weaver;

import org.lara.interpreter.weaver.utils.LaraResourceProvider;

/**
 * @author Joao Bispo
 *
 */
public enum LaraApiResource implements LaraResourceProvider {

    // AUTOPAR
    ADDITIONAL_CONDITIONS_CHECK("autopar/additionalConditionsCheck.lara"),
    ADD_OPENMP_DIRECTIVES("autopar/AddOpenMPDirectivesForLoop.lara"),
    ADD_PRAGMA_LOOP_INDEX("autopar/AddPragmaLoopIndex.lara"),
    AUTOPAR_STATS("autopar/AutoParStats.lara"),
    AUTOPAR_UTILS("autopar/AutoParUtils.lara"),
    AUTOPAR_1("autopar/BuildPetitFileInput.lara"),
    AUTOPAR_2("autopar/checkForFunctionCalls.lara"),
    AUTOPAR_3("autopar/checkForInvalidStmts.lara"),
    AUTOPAR_4("autopar/checkForOpenMPCanonicalForm.lara"),
    AUTOPAR_4_1("autopar/CheckForSafeFunctionCall.lara"),
    AUTOPAR_5("autopar/checkvarreReduction.lara"),
    AUTOPAR_6("autopar/ExecPetitDependencyTest.lara"),
    AUTOPAR_7("autopar/FindReductionArrays.lara"),
    AUTOPAR_8("autopar/get_varTypeAccess.lara"),
    AUTOPAR_9("autopar/InlineFunctionCalls.lara"),
    AUTOPAR_9_1("autopar/LoopInductionVariables.lara"),
    AUTOPAR_10("autopar/NormalizedBinaryOp.lara"),
    AUTOPAR_10_1("autopar/OmegaConfig.lara"),
    AUTOPAR_17("autopar/Parallelize.lara"),
    AUTOPAR_11("autopar/ParallelizeLoop.lara"),
    AUTOPAR_12("autopar/RemoveNakedloops.lara"),
    AUTOPAR_12_1("autopar/RemoveOpenMPfromInnerloop.lara"),
    AUTOPAR_12_2("autopar/RunInlineFunctionCalls.lara"),
    AUTOPAR_13("autopar/SetArrayAccessOpenMPscoping.lara"),
    AUTOPAR_14("autopar/SetMemberAccessOpenMPscoping.lara"),
    AUTOPAR_15("autopar/SetVariableAccess.lara"),
    AUTOPAR_16("autopar/SetVarrefOpenMPscoping.lara"),

    // Analysis
    ANALYSIS_ANALYSERS_BOUNDS_ANALYSER("analysis/analysers/BoundsAnalyser.lara"),
    ANALYSIS_ANALYSERS_BOUNDS_RESULT("analysis/analysers/BoundsResult.lara"),
    ANALYSIS_ANALYSERS_DOUBLE_FREE_ANALYSER("analysis/analysers/DoubleFreeAnalyser.lara"),
    ANALYSIS_ANALYSERS_DOUBLE_FREE_RESULT("analysis/analysers/DoubleFreeResult.lara"),
    ANALYSIS_CHECKERS_CHGRP_CHECKER("analysis/checkers/ChgrpChecker.lara"),
    ANALYSIS_CHECKERS_CHMOD_CHECKER("analysis/checkers/ChmodChecker.lara"),
    ANALYSIS_CHECKERS_CHOWN_CHECKER("analysis/checkers/ChownChecker.lara"),
    ANALYSIS_CHECKERS_CIN_CHECKER("analysis/checkers/CinChecker.lara"),
    ANALYSIS_CHECKERS_EXEC_CHECKER("analysis/checkers/ExecChecker.lara"),
    ANALYSIS_CHECKERS_FPRINTF_CHECKER("analysis/checkers/FprintfChecker.lara"),
    ANALYSIS_CHECKERS_FSCANF_CHECKER("analysis/checkers/FscanfChecker.lara"),
    ANALYSIS_CHECKERS_GETS_CHECKER("analysis/checkers/GetsChecker.lara"),
    ANALYSIS_CHECKERS_LAMBDA_CHECKER("analysis/checkers/LambdaChecker.lara"),
    ANALYSIS_CHECKERS_MEMCPY_CHECKER("analysis/checkers/MemcpyChecker.lara"),
    ANALYSIS_CHECKERS_PRINTF_CHECKER("analysis/checkers/PrintfChecker.lara"),
    ANALYSIS_CHECKERS_SCANF_CHECKER("analysis/checkers/ScanfChecker.lara"),
    ANALYSIS_CHECKERS_SPRINTF_CHECKER("analysis/checkers/SprintfChecker.lara"),
    ANALYSIS_CHECKERS_STRCAT_CHECKER("analysis/checkers/StrcatChecker.lara"),
    ANALYSIS_CHECKERS_STRCPY_CHECKER("analysis/checkers/StrcpyChecker.lara"),
    ANALYSIS_CHECKERS_SYSLOG_CHECKER("analysis/checkers/SyslogChecker.lara"),
    ANALYSIS_CHECKERS_SYSTEM_CHECKER("analysis/checkers/SystemChecker.lara"),


    // Code
    GLOBAL_VARIABLE("code/GlobalVariable.lara"),

    // Gprofer
    GPROFER("gprofer/Gprofer.lara"),
    GPROFER_ASPECTS("gprofer/_GproferAspects.lara"),

    // Hdf5
    HDF5("hdf5/Hdf5.lara"),

    // HLS
    HLS("hls/HLSAnalysis.lara"),
    HLS_TRACE("hls/TraceInstrumentation.lara"),
    HLS_MATH("hls/MathAnalysis.lara"),
    HLS_MATH_H("hls/MathHInfo.lara"),

    // MPI
    MPI_ACCESS_PATTERN("mpi/MpiAccessPattern.lara"),
    MPI_SCATTER_GATHER_LOOP("mpi/MpiScatterGatherLoop.lara"),
    MPI_UTILS("mpi/MpiUtils.lara"),

    // MPI Patterns
    ITERATION_VARIABLE_PATTERN("mpi/patterns/IterationVariablePattern.lara"),
    MPI_ACCESS_PATTERNS("mpi/patterns/MpiAccessPatterns.lara"),
    SCALAR_PATTERN("mpi/patterns/ScalarPattern.lara"),

    // OpenCL
    OPENCL_CALL("opencl/OpenCLCall.lara"),
    OPENCL_CALL_VARIABLES("opencl/OpenCLCallVariables.lara"),
    OPENCL_KERNEL_REPLACER("opencl/KernelReplacer.lara"),
    OPENCL_KERNEL_REPLACER_AUTO("opencl/KernelReplacerAuto.lara"),

    // Parser
    BATCH_PARSER("parser/BatchParser.lara"),

    // Stats
    OPS_BLOCK("stats/OpsBlock.lara"),
    OPS_COST("stats/OpsCost.lara"),
    OPS_COUNTER("stats/OpsCounter.lara"),
    STATIC_OPS_COUNTER("stats/StaticOpsCounter.lara"),

    // Memoization
    MEMOI_PROF("memoi/MemoiProf.lara"),
    // MEMOI_PROF_HELPER("memoi/_MemoiProfHelper.lara"),
    MEMOI_GEN("memoi/MemoiGen.lara"),
    MEMOI_GEN_HELPER("memoi/_MemoiGenHelper.lara"),
    MEMOI_TARGET("memoi/MemoiTarget.lara"),
    MEMOI_UTILS("memoi/MemoiUtils.lara"),
    MEMOI_ANALYSIS("memoi/MemoiAnalysis.lara"),

    // Clava utils
    CLAVA_DATA_STORE("util/ClavaDataStore.lara"),
    FILE_ITERATOR("util/FileIterator.lara"),
    SINGLE_FILE("util/SingleFile.lara"),

    // UVE
    UVE("uve/UVE.lara"),
    UVE_DETECT("uve/DetectStream.lara"),

    // Static objects

    _CLAVA_JAVA_TYPES("_ClavaJavaTypes.lara"),
    CLAVA("Clava.lara"),
    CLAVA_ASPECTS("ClavaAspects.lara"),
    CLAVA_CODE("ClavaCode.lara"),
    CLAVA_JOIN_POINTS("ClavaJoinPoints.lara"),
    CLAVA_TYPE("ClavaType.lara");

    private final String resource;

    private static final String WEAVER_PACKAGE = "clava/";
    private static final String BASE_PACKAGE = "clava/";

    /**
     * @param resource
     */
    private LaraApiResource(String resource) {
        this.resource = WEAVER_PACKAGE + getSeparatorChar() + BASE_PACKAGE + resource;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.suikasoft.SharedLibrary.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getOriginalResource() {
        return resource;
    }

}
