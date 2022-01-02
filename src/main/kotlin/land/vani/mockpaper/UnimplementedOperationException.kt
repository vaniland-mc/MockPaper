package land.vani.mockpaper

import org.opentest4j.TestAbortedException

/**
 * Sometimes your code may use a method that is not yet implemented in MockPaper.
 *
 * Then this happens MockPaper will, instead o returning placeholder values, throw an [UnimplementedOperationException].
 *
 * This is a subclass of [TestAbortedException] and causes your test to be skipped instead of just failing.
 */
class UnimplementedOperationException
@JvmOverloads
constructor(override val message: String? = null) : TestAbortedException()
