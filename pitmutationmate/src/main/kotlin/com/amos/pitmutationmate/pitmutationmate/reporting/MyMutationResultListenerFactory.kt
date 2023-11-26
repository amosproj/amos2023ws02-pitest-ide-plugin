package com.amos.pitmutationmate.pitmutationmate.reporting

import org.pitest.mutationtest.ListenerArguments
import org.pitest.mutationtest.MutationResultListener
import org.pitest.mutationtest.MutationResultListenerFactory
import java.util.*

class MyMutationResultListenerFactory : MutationResultListenerFactory  {
    override fun description(): String = "Custom MutationResulListenerFactory for generating custom MutationResultListener"

    override fun getListener(props: Properties?, args: ListenerArguments?): MutationResultListener {
        return MyMutationResultListener()
    }

    override fun name(): String = "MyMutationResultListenerFactory"
}