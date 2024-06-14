package com.example.todonotion.ui.todoSearchResult

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todonotion.ui.main.ADD_EDIT_RESULT_OK
import com.example.todonotion.ui.main.DELETE_RESULT_OK
import com.example.todonotion.Event
import com.example.todonotion.R
import com.example.todonotion.data.keyword.Keyword
import com.example.todonotion.data.keyword.KeywordsRepository
import com.example.todonotion.ui.KeywordsFilterType
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.Locale
import javax.inject.Inject

//https://stackoverflow.com/questions/60489319/notfoundexception-string-resource-id-0x0-when-binding-string-resource
//https://github.com/google-developer-training/advanced-android-testing/blob/starter_code/app/src/main/java/com/example/android/architecture/blueprints/todoapp/data/source/DefaultTasksRepository.kt
class KeywordViewModel @Inject constructor(private val keywordsRepository: KeywordsRepository) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allKeys: LiveData<List<Keyword>> = keywordsRepository.getAllKeywordsStream().asLiveData()

    //filter keyword list when input value change
    private val _filteredKeyWords = MutableLiveData<List<Keyword>>()
    val filteredKeywords: LiveData<List<Keyword>> = _filteredKeyWords

    private val _keyword = MutableLiveData<Keyword>()
    val keyword: LiveData<Keyword> = _keyword

    private val _storeKeyword = MutableLiveData<String>()
    val storeKeyword: LiveData<String> = _storeKeyword

    private var currentFiltering = KeywordsFilterType.ALL_KEYWORDS

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noKeywordsLabel = MutableLiveData<Int>()
    val noKeywordsLabel: LiveData<Int> = _noKeywordsLabel

    private val _noKeywordIconRes = MutableLiveData<Int>()
    val noKeywordIconRes: LiveData<Int> = _noKeywordIconRes

    private val _keywordsAddViewVisible = MutableLiveData<Boolean>()
    val keywordsAddViewVisible: LiveData<Boolean> = _keywordsAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private var resultMessageShown: Boolean = false

    init {
        // Set initial state
        setFiltering(KeywordsFilterType.ALL_KEYWORDS)
    }




    //https://stackoverflow.com/questions/70745324/how-to-print-size-of-flow-in-kotlin
    private suspend fun checkIsEmpty() : Boolean {
        return allKeys.value!!.isEmpty()
    }

    fun keywordIsEmpty(): Boolean {
        var isEmpty = false
        viewModelScope.launch {
            isEmpty = checkIsEmpty()
        }
        return isEmpty
    }


    private fun updateKeyword(keyword: Keyword) {
        val newKeyword = getUpdateKeyword(keywordId = keyword.id, keywordName = keyword.keyName, keywordComplete = !keyword.isCompleted)
        // Log.d("keywordClick", newKeyword.keyName +" " + newKeyword.isCompleted)
        if (keyword.isCompleted) {
            //change state
            viewModelScope.launch {
                keywordsRepository.updateKeyword(newKeyword)
                showSnackbarMessage(R.string.keyword_marked_complete)

            }
        } else {
            viewModelScope.launch {
                keywordsRepository.updateKeyword(newKeyword)
                showSnackbarMessage(R.string.keyword_marked_active)
            }
        }
    }

    /**
     * Inserts the new Item into database.
     */
    fun addNewKey(keyName: String) {
        val newItem = getNewKeyEntry(keyName)
        insertKey(newItem)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertKey(keyword: Keyword) {
        viewModelScope.launch {
            keywordsRepository.insertKeyword(keyword)
        }
    }


    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteKey(keyword: Keyword) {
        viewModelScope.launch {
            keywordsRepository.deleteKeyword(keyword)
            showSnackbarMessage(R.string.completed_keywords_cleared)
        }
    }

    /**
     * Retrieve an item from the repository.
     */

    fun filterByKeyWords(text: String) {
        //creating a new array list to filter our data
        val filteredList = ArrayList<Keyword>()

        // running a for loop to compare elements.
        for (item in allKeys.value!!) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.keyName.lowercase().contains(text.lowercase(Locale.getDefault()))) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item)
            }
        }

        _filteredKeyWords.value = filteredList
    }

    fun filteredKeyCount(): Int {
        return filteredKeywords.value!!.size
    }


    //https://stackoverflow.com/questions/46662513/convert-array-to-list-in-kotlin
    fun onKeyWordClicked(keyword: Keyword) {
        // TODO: Set the todo object
        _keyword.value = keyword
        updateKeyword(keyword)
    }

    //store keyword
    fun storeWordAction(text: String) {
        _storeKeyword.value = text
    }


    /**
     * Returns an instance of the [Item] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewKeyEntry(keyName: String): Keyword {
        return Keyword(
            keyName = keyName
        )
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be [KeywordsFilterType.ALL_KEYWORDS],
     * [KeywordsFilterType.COMPLETED_KEYWORDS], or
     * [KeywordsFilterType.ACTIVE_KEYWORDS]
     */
    fun setFiltering(requestType: KeywordsFilterType) {
        currentFiltering = requestType

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        when (requestType) {
            KeywordsFilterType.ALL_KEYWORDS -> {
                setFilter(
                    R.string.label_all, R.string.no_keywords_all,
                    R.drawable.ic_cruelty_free_24, true
                )
            }

            KeywordsFilterType.ACTIVE_KEYWORDS -> {
                setFilter(
                    R.string.label_active, R.string.no_keywords_active,
                    R.drawable.ic_check_circle_24, false
                )
            }

            KeywordsFilterType.COMPLETED_KEYWORDS -> {
                setFilter(
                    R.string.label_completed, R.string.no_keywords_completed,
                    R.drawable.ic_verified_user_24, false
                )
            }
        }
        // Refresh list
        allKeys.value?.let { filterKeywords(it, requestType) }
    }


    private fun setFilter(
        @StringRes filteringLabelString: Int, @StringRes noKeywordsLabelString: Int,
        @DrawableRes noKeywordIconDrawable: Int, keywordsAddVisible: Boolean
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noKeywordsLabel.value = noKeywordsLabelString
        _noKeywordIconRes.value = noKeywordIconDrawable
        _keywordsAddViewVisible.value = keywordsAddVisible
    }

    fun showEditResultMessage(result: Int) {
        if (resultMessageShown) return

        when (result) {
            ADD_EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_added_keyword_message)
            DELETE_RESULT_OK -> showSnackbarMessage(R.string.successfully_deleted_keyword_message)
        }
        resultMessageShown = true
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    fun filterKeywords(keywords: List<Keyword>, filteringType: KeywordsFilterType) {
        val keywordsToShow = ArrayList<Keyword>()
        // We filter the tasks based on the requestType
        for (keyword in keywords) {
            when (filteringType) {
                KeywordsFilterType.ALL_KEYWORDS -> keywordsToShow.add(keyword)
                KeywordsFilterType.ACTIVE_KEYWORDS -> if (keyword.isActive) {
                    keywordsToShow.add(keyword)
                }

                KeywordsFilterType.COMPLETED_KEYWORDS -> if (keyword.isCompleted) {
                    keywordsToShow.add(keyword)
                }
            }
        }
        _filteredKeyWords.value = keywordsToShow
    }

    private fun getUpdateKeyword(keywordId: Long, keywordName: String, keywordComplete: Boolean): Keyword {
        return Keyword(
            id = keywordId,
            keywordName,
            isCompleted = keywordComplete
        )
    }

    /**
     * Factory for [KeyViewModel] that takes [KeywordsRepository] as a dependency
     */
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}