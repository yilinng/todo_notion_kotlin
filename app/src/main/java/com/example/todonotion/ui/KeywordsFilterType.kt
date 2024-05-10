package com.example.todonotion.ui
/**
 * Used with the filter spinner in the keywords list.
 */
enum class KeywordsFilterType {
    /**
     * Do not filter tasks.
     */
    ALL_KEYWORDS,

    /**
     * Filters only the active (not completed yet) tasks.
     */
    ACTIVE_KEYWORDS,

    /**
     * Filters only the completed tasks.
     */
    COMPLETED_KEYWORDS
}