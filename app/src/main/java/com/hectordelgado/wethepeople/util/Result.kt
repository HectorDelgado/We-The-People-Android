package com.hectordelgado.wethepeople.util

/**
 *  We The People
 *
 *  @author Hector Delgado
 *
 *  Created on June 01, 2020.
 *  Copyright © 2020 Hector Delgado. All rights reserved.
 */
sealed class Result<out S, out F>

data class Success<out S>(val value: S) : Result<S, Nothing>()
data class Failure<out F>(val reason: F) : Result<Nothing, F>()