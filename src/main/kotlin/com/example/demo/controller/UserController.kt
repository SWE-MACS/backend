package com.example.demo.controller

import com.example.demo.dto.UserDeviceDTO
import com.example.demo.dto.user.UserCreateRequestDTO
import com.example.demo.dto.user.UserDTO
import com.example.demo.exception.UserNotFoundException
import com.example.demo.model.User
import com.example.demo.service.UserService
import com.example.demo.service.sendbird.SendbirdUserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1")
class UserController(
    private val userService: UserService,
    private val sendbirdUserService: SendbirdUserService
) {

    @PostMapping("/signup")
    fun createUser(@RequestBody request: UserCreateRequestDTO): ResponseEntity<UserDTO> {
        val user = User(
            id = UUID.randomUUID(),
            name = request.name,
            password = request.password,
            birthDate = request.birthDate,
            phoneNumber = request.phoneNumber
        )
        sendbirdUserService.createUser(user.id.toString(), request.name, "")
        return ResponseEntity(userService.createUser(user), HttpStatus.CREATED)
    }

    @DeleteMapping("/users/{userId}")
    fun deleteUser(
        @PathVariable userId: UUID
    ): ResponseEntity<String> {
        sendbirdUserService.deleteUser(userId.toString())
        userService.deleteUser(userId)

        return ResponseEntity<String>("User deleted successfully", HttpStatus.OK)
    }

    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<UserDTO>> {
        // User 리스트를 가져오고 DTO로 변환
        val users: List<User> = userService.getAllUsers()

        return users.map { user ->
            UserDTO(
                id = user.id,
                name = user.name,
                birthDate = user.birthDate,
                phoneNumber = user.phoneNumber,
            )
        }.let { ResponseEntity(it, HttpStatus.OK) }
    }

    @PostMapping("/user")
    fun getUser(@RequestBody request: String): ResponseEntity<UserDTO> {
        val mapper = ObjectMapper()
        val phoneNumber = mapper.readTree(request).get("phone_number").asText()

        // User 리스트를 가져오고 DTO로 변환
        val user: User = userService.getUserByPhoneNumber(phoneNumber).get()

        return UserDTO(
            id = user.id,
            name = user.name,
            birthDate = user.birthDate,
            phoneNumber = user.phoneNumber,
        ).let { ResponseEntity(it, HttpStatus.OK) }
    }

    @GetMapping("/users/{userId}/devices")
    fun getUserDevices(@PathVariable userId: UUID): ResponseEntity<List<UserDeviceDTO>> {

        val user = userService.getUser(userId)  // 유저 조회 (UserNotFoundException 예외 발생 가능)

        // 유저가 존재하면 해당 유저의 userDevices 정보를 가져와서 반환
        val userDevicesDTO = user.userDevices.map { userDevice ->
            // UserDevice를 통해 DeviceDTO로 변환
            UserDeviceDTO(
                category = userDevice.device.category.name,  // DeviceCategory의 이름
                deviceId = userDevice.device.id,  // Device의 ID
                deviceName = userDevice.device.productNumber // 장치 이름
            )
        }

        // 유저의 장치 목록 반환
        return ResponseEntity(userDevicesDTO, HttpStatus.OK)
    }
}
