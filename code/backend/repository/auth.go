package repository

import (
	"asprak/backend/domain"
	"asprak/backend/model"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

type AuthRepository struct {
	db *gorm.DB
}

func NewAuthRepository(
	db *gorm.DB,
) *AuthRepository {
	return &AuthRepository{
		db: db,
	}
}

func (r *AuthRepository) Login(data domain.User) (*string, error) {
	var user model.User
	if err := r.db.Where("email = ?", data.Email).First(&user).Error; err != nil {
		return nil, err
	}

	if user.Password != data.Password {
		return nil, gorm.ErrRecordNotFound
	}

	token := uuid.NewString()

	if err := r.db.Create(&model.Session{
		UserId: user.ID,
		Token:  token,
	}).Error; err != nil {
		return nil, err
	}

	return &token, nil
}

func (r *AuthRepository) Register(data domain.User) (*string, error) {
	user := data.ToModel()
	if err := r.db.Create(&user).Error; err != nil {
		return nil, err
	}

	token := uuid.NewString()

	if err := r.db.Create(&model.Session{
		UserId: user.ID,
		Token:  token,
	}).Error; err != nil {
		return nil, err
	}

	return &token, nil
}
