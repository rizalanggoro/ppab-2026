package domain

import (
	"asprak/backend/model"
	"time"
)

type Category struct {
	Id        uint      `json:"id"`
	UserId    uint      `json:"user_id"`
	Name      string    `json:"name"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
} // @name Category

func FromCategoryModel(m *model.Category) *Category {
	return &Category{
		Id:        m.ID,
		UserId:    m.UserId,
		Name:      m.Name,
		CreatedAt: m.CreatedAt,
		UpdatedAt: m.UpdatedAt,
	}
}

func (m *Category) ToModel() *model.Category {
	return &model.Category{
		UserId: m.UserId,
		Name:   m.Name,
	}
}
