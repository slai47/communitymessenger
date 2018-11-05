package com.slai.communitymessenger.handlers

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.text.TextUtils
import com.slai.communitymessenger.model.Contact
import androidx.core.content.ContextCompat.startActivity
import android.net.Uri.withAppendedPath
import android.content.Intent.ACTION_VIEW
import android.net.Uri


class ContactHandler(val context : Context) {

    fun getContacts() : HashMap<Int, Contact> {
        val list = HashMap<Int, Contact>()

        val c = context.contentResolver.query(
            android.provider.ContactsContract.Contacts.CONTENT_URI,
            arrayOf<String>(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts.DISPLAY_NAME
            ),
            ContactsContract.Contacts.HAS_PHONE_NUMBER, null,
            ContactsContract.Contacts.DISPLAY_NAME
        )

        c.use { cursor ->
            if(cursor.moveToFirst()){
                do {
                    val id : Int = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val photoId : Int = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID))
                    val name : String = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                    val contact = Contact(name, id)
                    contact.photoId = photoId
                    list[id] = contact
                } while (cursor.moveToNext())
            }
        }

        return list
    }

    fun searchContact(search : String) : List<Contact> {
        val contacts : ArrayList<Contact> = ArrayList()

        val isDigit = TextUtils.isDigitsOnly(search)
        var columnCompare = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        if(isDigit){
            columnCompare = ContactsContract.CommonDataKinds.Phone.NUMBER
        }

        val c = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER ),
            "$columnCompare LIKE '%$search%'", null,
            null
        )

        c.use { cursor ->
            if(cursor.moveToFirst()){
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    val contact = Contact(name, id)
                    contact.number = number

                    contacts.add(contact)
                } while (cursor.moveToNext())
            }
        }
        return contacts
    }

    fun getContactInfo(id : Int) : Contact? {
        var contact : Contact? = null

        val c = context.contentResolver.query(
            android.provider.ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts.DISPLAY_NAME
            ),
            "${ContactsContract.Contacts._ID}=$id", null,
            ContactsContract.Contacts.DISPLAY_NAME
        )

        c.use {cursor ->
            if(cursor.moveToFirst()){
                val id : Int = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val photoId : Int = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID))
                val name : String = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                contact = Contact(name, id)
                contact!!.photoId = photoId
            }
        }

        return contact
    }

    fun openProfile(id : Int){
        val intent = Intent(Intent.ACTION_VIEW)
        val contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, "$id")
        intent.setData(contactUri)
        context.startActivity(intent)
    }

}