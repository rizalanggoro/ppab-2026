package model

import "gorm.io/gorm"

type Todo struct {
	gorm.Model

	CategoryId  uint
	Category    Category `gorm:"constraint:OnUpdate:CASCADE,OnDelete:CASCADE;"`
	Title       string
	Description string
	Completed   bool
}
