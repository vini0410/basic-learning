package com.study.MementoApp.controller

import com.study.MementoApp.dto.CreateProfileRequest
import com.study.MementoApp.dto.MementoResponse
import com.study.MementoApp.dto.UpdateProfileRequest
import com.study.MementoApp.dto.UserProfileResponse
import com.study.MementoApp.service.UserProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/profiles")
class UserProfileController(
    private val profileService: UserProfileService
) {

    @PostMapping
    fun createProfile(@RequestBody request: CreateProfileRequest): ResponseEntity<UserProfileResponse> {
        val response = profileService.createProfile(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getProfile(@PathVariable id: String): ResponseEntity<UserProfileResponse> {
        val response = profileService.getProfile(id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateProfile(
        @PathVariable id: String,
        @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<UserProfileResponse> {
        val response = profileService.updateProfile(id, request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/undo")
    fun undoUpdate(@PathVariable id: String): ResponseEntity<UserProfileResponse> {
        val response = profileService.undoUpdate(id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/restore/{version}")
    fun restoreVersion(
        @PathVariable id: String,
        @PathVariable version: Int
    ): ResponseEntity<UserProfileResponse> {
        val response = profileService.restoreVersion(id, version)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/history")
    fun getHistory(@PathVariable id: String): ResponseEntity<List<MementoResponse>> {
        val history = profileService.getHistory(id)
        return ResponseEntity.ok(history)
    }
}
