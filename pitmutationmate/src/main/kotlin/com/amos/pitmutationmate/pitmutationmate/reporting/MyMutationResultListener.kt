package com.amos.pitmutationmate.pitmutationmate.reporting


import org.pitest.mutationtest.ClassMutationResults
import org.pitest.mutationtest.MutationResultListener

class MyMutationResultListener : MutationResultListener {
    override fun runStart() {
        // Initialization of logic or needed classes before the mutation testing run
    }

    override fun handleMutationResult(results: ClassMutationResults?) {
        // Process and handle each mutation Result
        val value = 6
    }

    override fun runEnd() {
        // Cleanup or final logic after the mutation test run
        XMLParser().loadResultsFromXmlReport("needtofindpath")
    }

}