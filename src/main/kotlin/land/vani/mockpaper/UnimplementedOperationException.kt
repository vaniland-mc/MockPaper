package land.vani.mockpaper

import org.opentest4j.TestAbortedException

class UnimplementedOperationException(override val message: String? = null) : TestAbortedException()
