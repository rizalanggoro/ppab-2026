package repository

import (
	"asprak/backend/domain"
	"asprak/backend/model"

	"gorm.io/gorm"
)

type CategoryRepository struct {
	db *gorm.DB
}

func NewCategoryRepository(
	db *gorm.DB,
) *CategoryRepository {
	return &CategoryRepository{
		db: db,
	}
}

func (r *CategoryRepository) Create(data domain.Category) (*domain.Category, error) {
	category := data.ToModel()
	if err := r.db.Create(&category).Error; err != nil {
		return nil, err
	}

	return domain.FromCategoryModel(category), nil
}

type GetAllCategoriesFilter struct {
	UserId uint
}

func (r *CategoryRepository) GetAll(filter GetAllCategoriesFilter) ([]domain.Category, error) {
	var categories []model.Category
	if err := r.db.Where("user_id = ?", filter.UserId).
		Order("name asc").
		Find(&categories).Error; err != nil {
		return nil, err
	}

	var result []domain.Category
	for _, category := range categories {
		result = append(result, *domain.FromCategoryModel(&category))
	}

	return result, nil
}

func (r *CategoryRepository) Delete(categoryId uint) error {
	if err := r.db.Delete(&model.Category{}, categoryId).Error; err != nil {
		return err
	}

	return nil
}
