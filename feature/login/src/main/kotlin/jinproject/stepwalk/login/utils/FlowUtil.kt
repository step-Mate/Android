package jinproject.stepwalk.login.utils

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(FlowPreview::class)
fun StateFlow<String>.debouncedFilter(millis : Long) =
    this.debounce(millis)
    .filter { it.isNotEmpty() }
    .distinctUntilChanged()