package com.example.fragment

import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL

const val url = "https://drive.google.com/u/0/uc?id=1-KO-9GA3NzSgIc1dkAsNm8Dqw0fuPxcR&export=download"

class ContentFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }
    private var contactsJson = arrayListOf<Contact>()
    private var contacts = arrayListOf<Contact>()
    private lateinit var adapter: Adapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        Thread {
            val connection = URL(url).openConnection() as HttpURLConnection
            val jsonData = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            contactsJson = Gson().fromJson(jsonData, Array<Contact>::class.java).toList() as ArrayList<Contact>
            contacts.addAll(contactsJson)
        }.start()

        val adapter = Adapter(view.context, contacts)
        val recyclerView: RecyclerView = view.findViewById(R.id.rView)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = adapter
    }

    fun search(request: CharSequence)
    {
        contacts.clear()
        if(request.isNotBlank()){
            for(contact in contactsJson){
                if ((contact.name.contains(request)) or (contact.phone.contains(request)) or (contact.type.contains(request))){
                    contacts.add(contact)
                }
            }
        }
        else {
            contacts.addAll(contactsJson)
        }
        adapter.notifyDataSetChanged()
    }
}