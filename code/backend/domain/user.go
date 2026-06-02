package domain

import (
	"asprak/backend/model"
	"time"
)

type User struct {
	Id        uint      `json:"id"`
	Name      string    `json:"name"`
	Email     string    `json:"email"`
	Password  string    `json:"password"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
} // @name User

func FromUserModel(m *model.User) *User {
	return &User{
		Id:        m.ID,
		Name:      m.Name,
		Email:     m.Email,
		Password:  m.Password,
		CreatedAt: m.CreatedAt,
		UpdatedAt: m.UpdatedAt,
	}
}

func (m *User) ToModel() *model.User {
	return &model.User{
		Name:     m.Name,
		Email:    m.Email,
		Password: m.Password,
	}
}
