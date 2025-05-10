package sharedtype

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestMain(t *testing.T) {
	dependencyClassC1.A = dependencyClassA1

	RecursiveClass1.DirectRef = &RecursiveClass{
		DirectRef: nil,
		ArrayRef:  []RecursiveClass{},
	}

	OptionalMethods1.MapNestedValueOptional = &map[int32]string{1: "foo"}
	OptionalMethods1.SetNestedValueOptional = &[]string{"foo", "bar"}
	OptionalMethods1.NestedValueOptional = &List1[0]
}

func TestConstants(t *testing.T) {
	assert.Equal(t, float32(1.888), FLOAT_VALUE)
	assert.Equal(t, "MilkyWay", REFERENCED_ENUM_VALUE)
	assert.Equal(t, int32(1), MyEnumConstants.INT_VALUE)
}
