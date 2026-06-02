package domain

import (
	"asprak/backend/model"
	"time"
)

type Todo struct {
	Id          uint      `json:"id"`
	CategoryId  uint      `json:"category_id"`
	Title       string    `json:"title"`
	Description string    `json:"description"`
	Completed   bool      `json:"completed"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
} // @name Todo

func FromTodoModel(m *model.Todo) *Todo {
	return &Todo{
		Id:          m.ID,
		CategoryId:  m.CategoryId,
		Title:       m.Title,
		Description: m.Description,
		Completed:   m.Completed,
		CreatedAt:   m.CreatedAt,
		UpdatedAt:   m.UpdatedAt,
	}
}

func (m *Todo) ToModel() *model.Todo {
	return &model.Todo{
		CategoryId:  m.CategoryId,
		Title:       m.Title,
		Description: m.Description,
		Completed:   m.Completed,
	}
}
