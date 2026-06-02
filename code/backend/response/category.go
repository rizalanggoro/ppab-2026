package response

import "asprak/backend/dto"

type CreateCategory struct {
	Item dto.Category `json:"item"`
} // @name CreateCategoryRes

type GetAllCategories struct {
	Items []dto.Category `json:"items"`
} // @name GetAllCategoriesRes

type DeleteCategory struct {
	Message string `json:"message"`
} // @name DeleteCategoryRes
