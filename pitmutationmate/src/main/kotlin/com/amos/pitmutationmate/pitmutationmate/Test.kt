// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate

import org.pitest.aggregate.ReportAggregatorResultListener
import org.pitest.plugin.ClientClasspathPlugin
import org.pitest.util.ServiceLoader

// Man muss den code auf den launchclasspath von pitest bekommem mit einem Serviceloader
// Interesting links:
// https://stackoverflow.com/questions/52204709/what-is-serviceloader-and-how-is-it-used
// https://pedrorijo.com/blog/java-service-loader/
// https://pitest.org/quickstart/advanced/
class Test : ClientClasspathPlugin{
    override fun description(): String {
        TODO("Not yet implemented")
        return "Test"
    }

    fun test() {
        val t = SL()
        // Iwie muss man das ausführen können auf den letzten pitest run.
        // Line coverage kann man hier nicht so leicht bekommen.
        // Vllt. kann man ein Listener schreiben der ähnlich zum ReportAggregatorResultListener ist, aber noch
        // line coverage auch zurück gibt.
        val aggListener = ReportAggregatorResultListener()
        val result = aggListener.result()
        result.testStrength
        result.mutationCoverage
        ServiceLoader.load(Test::class.java)
    }
}

class SL : ServiceLoader() {

}
