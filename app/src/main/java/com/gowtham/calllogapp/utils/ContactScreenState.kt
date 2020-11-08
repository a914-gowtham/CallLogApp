package com.gowtham.calllogapp.utils

sealed class ContactScreenState {

    object DeleteState : ContactScreenState(){
        override fun toString(): String {
            return "DeleteState"
        }
    }

    object SearchState : ContactScreenState(){
        override fun toString(): String {
            return "SearchState"
        }
    }

    object IdleState : ContactScreenState() {
        override fun toString(): String {
            return "IdleState"
        }
    }

}
