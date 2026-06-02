package dto

import "asprak/backend/domain"

type Todo struct {
	Data     domain.Todo     `json:"data"`
	Category domain.Category `json:"category"`
} // @name TodoDTO
