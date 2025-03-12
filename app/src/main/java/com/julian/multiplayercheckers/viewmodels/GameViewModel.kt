package com.julian.multiplayercheckers.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.julian.multiplayercheckers.dataclasses.GameData
import com.julian.multiplayercheckers.enums.FieldStates
import com.julian.multiplayercheckers.fragments.BOARD_SIZE
import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel.Companion.TOKENS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    application: Application,
    private val database: DatabaseReference
) : AndroidViewModel(application) {
    private val _canMove: MutableLiveData<Boolean> = MutableLiveData(false)
    val canMove: LiveData<Boolean> get() = _canMove
    val board: Array<IntArray> = Array(BOARD_SIZE) { IntArray(BOARD_SIZE) }

    fun observeBoard(gameToken: String) {
        val gameStateReference = database.child(TOKENS).child(gameToken)
        val gameStateListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gameData: GameData = snapshot.getValue(GameData::class.java)!!
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        gameStateReference.addValueEventListener(gameStateListener)
    }
}