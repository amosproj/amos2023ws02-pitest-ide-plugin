package com.amos.pitmutationmate.pitmutationmate.reporting

import org.pitest.mutationtest.ClassMutationResults
import org.pitest.mutationtest.DetectionStatus
import org.pitest.mutationtest.MutationResultListener
import org.pitest.mutationtest.engine.MutationDetails
import java.util.Optional

class MyMutationResultListener : MutationResultListener {
    private  var resultData = ResultData()

    override fun runStart() {
        resultData = ResultData()
    }

    override fun handleMutationResult(results: ClassMutationResults) {
        for (result in results.mutations) {
            val details: MutationDetails = result.details
            val status: DetectionStatus = result.status

            resultData.addMutationResult(
                MutationResult(
                    detected = status.isDetected,
                    status = status.name,
                    numberOfTestsRun = result.numberOfTestsRun,
                    sourceFile = details.filename,
                    mutatedClass = details.className.nameWithoutPackage.asJavaName(),
                    mutatedMethod = details.method,
                    methodDescription = details.id.location.methodDesc,
                    lineNumber = details.lineNumber,
                    mutator = details.mutator,
                    indexes = details.id.indexes.toList(),
                    block = details.block,
                    killingTest = result.killingTest,
                    description = details.description
                )
            )
        }
    }

    override fun runEnd() {
        // TODO If needed: Cleanup or final logic after the mutation test run
    }

    data class ResultData(
        val mutationResults: MutableList<MutationResult> = mutableListOf()
    ) {
        fun addMutationResult(mutationResult: MutationResult) {
            mutationResults.add(mutationResult)
        }
    }

    data class MutationResult(
        val detected: Boolean,
        val status: String,
        val numberOfTestsRun: Int,
        val sourceFile: String,
        val mutatedClass: String,
        val mutatedMethod: String,
        val methodDescription: String,
        val lineNumber: Int,
        val mutator: String,
        val indexes: List<Int>,
        val block: Int,
        val killingTest: Optional<String>,
        val description: String
    )
}
