package com.example.todonotion.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todonotion.data.Keyword.Keyword
import com.example.todonotion.data.Keyword.KeywordDao
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.Locale


class KeyViewModel (private val keywordDao: KeywordDao) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allKeys: LiveData<List<Keyword>> = keywordDao.getKeys().asLiveData()

    //filter keyword list when input value change
    private val _filteredKeyWords = MutableLiveData<List<Keyword>>()
    val filteredKeywords: LiveData<List<Keyword>> = _filteredKeyWords

    private val _keyword = MutableLiveData<Keyword>()
    val keyword: LiveData<Keyword> = _keyword

    private val _storeKeyword = MutableLiveData<String>()
    val storeKeyword: LiveData<String> = _storeKeyword

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
            keywordDao.insert(keyword)
        }
    }


    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteKey(keyword: Keyword) {
        viewModelScope.launch {
            keywordDao.delete(keyword)
        }
    }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveKey(id: Int): LiveData<Keyword> {
        return keywordDao.getKey(id).asLiveData()
    }

    fun retrieveKeyByName(keyName: String): LiveData<Keyword> {
        return keywordDao.getKeyByName(keyName).asLiveData()
    }


    fun filterByKeyWords(text: String){
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

    fun filteredKeyCount():Int {
        return filteredKeywords.value!!.size
    }


    //https://stackoverflow.com/questions/46662513/convert-array-to-list-in-kotlin
    fun onKeyWordClicked(keyword: Keyword) {
        // TODO: Set the todo object
        _keyword.value = keyword
    }

    //store keyword
    fun storeWordAction(text: String){
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

}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class KeyViewModelFactory(private val keywordDao: KeywordDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KeyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KeyViewModel(keywordDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}