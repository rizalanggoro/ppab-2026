package model

import "gorm.io/gorm"

type Category struct {
	gorm.Model

	UserId uint
	User   User   `gorm:"constraint:OnUpdate:CASCADE,OnDelete:CASCADE;"`
	Name   string `gorm:"unique"`
}
