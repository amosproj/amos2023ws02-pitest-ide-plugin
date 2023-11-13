package com.amos.pitmutationmate.pitmutationmate

import org.pitest.mutationtest.MutationResultListener
import org.pitest.mutationtest.MutationResultListenerFactory
import org.pitest.plugin.FeatureSetting
import java.util.Properties

class CustomMutationResultListenerFactory : MutationResultListenerFactory {

    override fun description(): String {
        return "Custom Mutation Result Listener Factory"
    }

    override fun getListener(properties: Properties?, featureSetting: FeatureSetting?): MutationResultListener {
        // Fetch HTML report and load key results into a data structure
        val htmlReportPath = properties?.getProperty("htmlReportPath") // Specify the path to your HTML report
        val resultDataStructure = loadResultsFromHtmlReport(htmlReportPath)

        // Create and return a custom MutationResultListener
        return CustomMutationResultListener(resultDataStructure)
    }

    private fun loadResultsFromHtmlReport(htmlReportPath: String?): ResultDataStructure {
        // Implement the logic to parse the HTML report and load key results into a data structure
        // For simplicity, let's assume ResultDataStructure is a data class representing your data structure
        // You need to implement it according to your requirements.
        return ResultDataStructure() // Placeholder implementation, replace with actual logic
    }

    // Your custom MutationResultListener implementation
    private class CustomMutationResultListener(private val resultDataStructure: ResultDataStructure) :
        MutationResultListener {

        // Implement MutationResultListener methods as needed
        // For example, you may want to override methods like handleMutationResult() to process mutation results
        // based on the loaded data structure.
    }

    // Define your ResultDataStructure data class according to your requirements
    private data class ResultDataStructure(
        val mutationInfos: List<MutationInfo>
    )

    private data class MutationInfo(
        val detected: Boolean,
        val status: String,
        val numberOfTestsRun: Int,
        val sourceFile: String,
        val mutatedClass: String,
        val lineNumber: Int,
        val mutatedMethod: String,
        val methodDescription: String,
        val mutator: String,
        val indexes: List<Int>,
        val killingTest: String,
        val description: String
    )
}