package sharedtype

import "testing"

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
