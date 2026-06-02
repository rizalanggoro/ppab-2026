package response

type Login struct {
	Token string `json:"token"`
} // @name LoginRes

type Register struct {
	Token string `json:"token"`
} // @name RegisterRes
