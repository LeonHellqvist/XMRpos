package org.monerokon.xmrpos.ui.settings.fiatcurrencies

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.monerokon.xmrpos.data.repository.DataStoreRepository
import org.monerokon.xmrpos.ui.Settings
import javax.inject.Inject

@HiltViewModel
class FiatCurrenciesViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    val fiatOptions = listOf("USD", "EUR", "CZK", "MXN")

    private var navController: NavHostController? = null

    fun setNavController(navController: NavHostController) {
        this.navController = navController
    }

    fun navigateToMainSettings() {
        navController?.navigate(Settings)
    }

    var primaryFiatCurrency: String by mutableStateOf("")

    var referenceFiatCurrencies: List<String> by mutableStateOf(emptyList())

    init {
        viewModelScope.launch {
            dataStoreRepository.getPrimaryFiatCurrency().collect { storedPrimaryFiatCurrency ->
                println("primaryFiatCurrency: $storedPrimaryFiatCurrency")
                primaryFiatCurrency = storedPrimaryFiatCurrency
            }
        }
        viewModelScope.launch {
            dataStoreRepository.getReferenceFiatCurrencies().collect { storedReferenceFiatCurrencies ->
                println("storedContactInformation: $storedReferenceFiatCurrencies")
                referenceFiatCurrencies = storedReferenceFiatCurrencies
            }
        }
    }

    fun updatePrimaryFiatCurrency(newPrimaryFiatCurrency: String) {
        primaryFiatCurrency = newPrimaryFiatCurrency
        viewModelScope.launch {
            dataStoreRepository.savePrimaryFiatCurrency(newPrimaryFiatCurrency)
        }
    }

    fun addReferenceFiatCurrency(newReferenceFiatCurrency: String) {
        referenceFiatCurrencies = referenceFiatCurrencies + newReferenceFiatCurrency
        viewModelScope.launch {
            dataStoreRepository.saveReferenceFiatCurrencies(referenceFiatCurrencies)
        }
    }

    fun removeReferenceFiatCurrency(index: Int) {
        referenceFiatCurrencies = referenceFiatCurrencies.toMutableList().apply { removeAt(index) }
        viewModelScope.launch {
            dataStoreRepository.saveReferenceFiatCurrencies(referenceFiatCurrencies)
        }
    }

    fun moveReferenceFiatCurrencyUp(index: Int) {
        if (index > 0) {
            referenceFiatCurrencies = referenceFiatCurrencies.toMutableList().apply {
                val temp = this[index]
                this[index] = this[index - 1]
                this[index - 1] = temp
            }
            viewModelScope.launch {
                dataStoreRepository.saveReferenceFiatCurrencies(referenceFiatCurrencies)
            }
        }
    }

    fun moveReferenceFiatCurrencyDown(index: Int) {
        if (index < referenceFiatCurrencies.size - 1) {
            referenceFiatCurrencies = referenceFiatCurrencies.toMutableList().apply {
                val temp = this[index]
                this[index] = this[index + 1]
                this[index + 1] = temp
            }
            viewModelScope.launch {
                dataStoreRepository.saveReferenceFiatCurrencies(referenceFiatCurrencies)
            }
        }
    }
}


