package com.amos.pitmutationmate.pitmutationmate.reporting


import org.pitest.mutationtest.ClassMutationResults
import org.pitest.mutationtest.MutationResultListener

class MyMutationResultListener : MutationResultListener {
    override fun runStart() {
        //TODO If needed: Initialization of logic or needed classes before the mutation testing run
    }

    override fun handleMutationResult(results: ClassMutationResults?) {
        //TODO: Process and handle each mutation Result
    }

    override fun runEnd() {
        //TODO If needed: Cleanup or final logic after the mutation test run
    }

}