package st.masoom.gemini

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = Constants.apiKey
    )

    fun sendMessage(question: String){
        viewModelScope.launch {
            try{
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role){
                            text(it.message) }
                    }.toList()
                )

                messageList.add(MessageModel(question,"user"))
                messageList.add(MessageModel("Typing.....","model"))

                val response= chat.sendMessage(question)
                messageList.removeLast()
                messageList.add(MessageModel(response.text.toString(),"model"))

                //Log.i("Response from Gemini",response.text.toString())
            }catch (e : Exception){
                messageList.removeLast()
                messageList.add(MessageModel(("Error : "+e.message.toString()), "model"))
            }

        }
    }
}

