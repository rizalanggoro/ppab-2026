package model

import "gorm.io/gorm"

type Session struct {
	gorm.Model

	UserId uint
	User   User   `gorm:"constraint:OnUpdate:CASCADE,OnDelete:CASCADE;"`
	Token  string `gorm:"unique"`
}
