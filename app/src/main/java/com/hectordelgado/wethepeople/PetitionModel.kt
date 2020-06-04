package com.hectordelgado.wethepeople

/**
 *  We The People
 *
 *  @author Hector Delgado
 *
 *  Created on May 28, 2020.
 *  Copyright © 2020 Hector Delgado. All rights reserved.
 */
data class PetitionModel(val id: String, val title: String, val body: String, val signatureCount: Int, val signaturesNeeded: Int) {
}