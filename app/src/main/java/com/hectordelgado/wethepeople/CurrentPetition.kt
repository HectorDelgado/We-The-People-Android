package com.hectordelgado.wethepeople

/**
 *  We The People
 *
 *  @author Hector Delgado
 *
 *  Created on June 01, 2020.
 *  Copyright © 2020 Hector Delgado. All rights reserved.
 */
object CurrentPetition {
    private var currentPetitions: PetitionModel? = null

    fun setPetition(petition: PetitionModel) {
        currentPetitions = PetitionModel(petition.id, petition.title, petition.body, petition.signatureCount, petition.signaturesNeeded)
    }

    fun getPetitions(): PetitionModel? {
        return currentPetitions
    }
}