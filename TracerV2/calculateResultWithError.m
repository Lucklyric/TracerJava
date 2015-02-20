%Function For Calculation the Property
function result = calculateResultWithError(error,array1,array2,array3)
digits(5);
result = [0.0;0.0;0.0];
for i = 1:numel(array1(:,1))
    if array1(i,1) <= error
        result(1,1) = result(1,1)/100.0*100.0 + array1(i,3);
    end
end

for i = 1:numel(array2(:,1))
    if array2(i,1) <= error
        result(2,1) = result(2,1)/100.0*100.0 + array2(i,3);
    end
end

for i = 1:numel(array3(:,1))
    if array3(i,1) <= error
        result(3,1) = result(3,1)/100.0*100.0 + array3(i,3);
    end
end
