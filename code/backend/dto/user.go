package dto

import "asprak/backend/domain"

type User struct {
	Data domain.User `json:"data"`
} // @name UserDTO
