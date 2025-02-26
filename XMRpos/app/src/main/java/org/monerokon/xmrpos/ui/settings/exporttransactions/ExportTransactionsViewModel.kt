package org.monerokon.xmrpos.ui.settings.exporttransactions

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.monerokon.xmrpos.data.local.room.model.Transaction
import org.monerokon.xmrpos.data.repository.TransactionRepository
import org.monerokon.xmrpos.ui.Settings
import java.io.OutputStreamWriter
import javax.inject.Inject

@HiltViewModel
class ExportTransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private var navController: NavHostController? = null

    fun setNavController(navController: NavHostController) {
        this.navController = navController
    }

    fun navigateToMainSettings() {
        navController?.navigate(Settings)
    }


    fun exportAllTransactions(context: Context, uri: Uri) {
        exportToUri(
            context = context,
            uri = uri
        )
    }

    fun deleteAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.deleteAllTransactions()
        }
    }

    fun exportToUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = transactionRepository.getAllTransactions() // Fetch data from Room
            writeDataToUri(context, uri, data)
        }
    }

    private fun writeDataToUri(context: Context, uri: Uri, data: List<Transaction>) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    // Write CSV headers
                    writer.append("Koinly Date,Amount,Currency,Label,TxHash\n")

                    // Write CSV rows
                    data.forEach { entity ->
                        // Format xmrAmount to 12 decimal places
                        val formattedAmount = String.format("%.12f", entity.xmrAmount)
                        // Format timestamp to Koinly Date
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm 'UTC'", Locale.getDefault())
                        val date = inputFormat.parse(entity.timestamp)
                        val formattedDate = outputFormat.format(date)
                        writer.append("$formattedDate,$formattedAmount,XMR,income,${entity.txId}\n")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}


