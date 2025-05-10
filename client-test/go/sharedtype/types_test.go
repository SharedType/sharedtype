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

var List1 = []EnumGalaxy{"Andromeda", "MilkyWay", "Triangulum"}
var Record1 = map[EnumTShirt]int32{
	S: 1,
	M: 2,
	L: 3,
}
var Size1 EnumSize = 1
var TshirtSize1 EnumTShirt = S

var dependencyClassC1 = &DependencyClassC{}

var dependencyClassB1 = &DependencyClassB{
	C: dependencyClassC1,
}

var dependencyClassA1 = &DependencyClassA{
	SuperClassA: SuperClassA{
		A:                           0,
		NotIgnoredImplementedMethod: 0,
		Value:                       0,
	},
	B: dependencyClassB1,
}

var Obj = JavaRecord[string]{
	BoxedBoolean:                  false,
	BoxedByte:                     0,
	BoxedChar:                     'a',
	BoxedDouble:                   0,
	BoxedFloat:                    0,
	BoxedInt:                      0,
	BoxedIntArray:                 []int32{},
	BoxedLong:                     0,
	BoxedShort:                    0,
	ContainerStringList:           []Container[string]{},
	ContainerStringListCollection: [][]Container[string]{},
	CyclicDependency:              dependencyClassA1,
	DuplicateAccessor:             "",
	EnumGalaxy:                    "MilkyWay",
	EnumSize:                      3,
	GenericList:                   []string{},
	GenericListSet:                [][]string{},
	GenericSet:                    []string{},
	IntArray:                      []int32{},
	Object:                        nil,
	PrimitiveBoolean:              false,
	PrimitiveByte:                 0,
	PrimitiveChar:                 'b',
	PrimitiveDouble:               0,
	PrimitiveFloat:                0,
	PrimitiveInt:                  0,
	PrimitiveLong:                 0,
	PrimitiveShort:                0,
	String:                        "",
	Value:                         "",
}

var AnotherJavaClass1 = &AnotherJavaClass{
	Value: 333,
}

var RecursiveClass1 = &RecursiveClass{
	DirectRef: &RecursiveClass{
		DirectRef: nil,
		ArrayRef:  []RecursiveClass{},
	},
	ArrayRef: []RecursiveClass{},
}

var MapClass1 = MapClass{
	MapField: map[int32]string{},
	EnumKeyMapField: map[EnumSize]string{
		1: "1",
	},
	CustomMapField: map[int32]string{
		55: "abc",
	},
	NestedMapField: map[string]map[string]int32{
		"M1": {
			"V": 1,
		},
	},
}

var OptionalMethods1 = OptionalMethod{
	ValueOptional:          nil,
	NestedValueOptional:    nil,
	SetNestedValueOptional: nil,
	MapNestedValueOptional: nil,
}

var ArrayClass1 = ArrayClass{
	Arr: []string{"abc"},
}

var JavaTime1 = JavaTimeClass{
	UtilDate:       "2022-01-01T00:00:00.000+08:00",
	SqlDate:        "2022-01-01T00:00:00.000+08:00",
	LocalDate:      "2022-01-01T00:00:00.000+08:00",
	LocalTime:      "2022-01-01T00:00:00.000+08:00",
	LocalDateTime:  "2022-01-01T00:00:00.000+08:00",
	ZonedDateTime:  "2022-01-01T00:00:00.000+08:00",
	OffsetDateTime: "2022-01-01T00:00:00.000+08:00",
	OffsetTime:     "2022-01-01T00:00:00.000+08:00",
	Instant:        "2022-01-01T00:00:00.000+08:00",
}

var JodaTime1 = JodaTimeClass{
	JodaDateTime:       "2022-01-01T00:00:00.000+08:00",
	JodaLocalDate:      "2022-01-01T00:00:00.000+08:00",
	JodaMonthDay:       "2022-01-01T00:00:00.000+08:00",
	JodaLocalTime:      "2022-01-01T00:00:00.000+08:00",
	JodaLocalDateTime:  "2022-01-01T00:00:00.000+08:00",
	JodaOffsetDateTime: "2022-01-01T00:00:00.000+08:00",
}

var MathClass1 = MathClass{
	BigDecimal: "1.1",
	BigInteger: "8888888555555",
}

var EnumConstValue1 EnumConstReference = 156
var EnumEnumValue1 EnumEnumReference = 3
