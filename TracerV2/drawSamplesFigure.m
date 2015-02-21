%drawSamplesFigure
%plot the histogram of the array;
%histogram(array),title('A VS B');
%normalize the data
normalized1 = (tabulate(sample1));
normalized_result1 = normalized1(:,[1 3]);
normalized2 = (tabulate(sample2));
normalized_result2 = normalized2(:,[1 3]);
normalized3 = (tabulate(sample3));
normalized_result3 = normalized3(:,[1 3]);
x1 = normalized_result1(:,1);
y1 = normalized_result1(:,2)/100;
x2 = normalized_result2(:,1);
y2 = normalized_result2(:,2)/100;
x3 = normalized_result3(:,1);
y3 = normalized_result3(:,2)/100;
%plot the normalized plot
figure;
subplot(2,2,1),bar(x1,y1),title('AB');
for i = 1:numel(x1)
    text(x1(i), y1(i) + 0.02, [num2str(y1(i)*100),'%'], 'VerticalAlignment', 'top', 'FontSize', 10,'Rotation',90)
end
subplot(2,2,2),bar(x2,y2),title('AC');
for i = 1:numel(x2)
    text(x2(i), y2(i) + 0.02, [num2str(y2(i)*100),'%'], 'VerticalAlignment', 'top', 'FontSize', 10,'Rotation',90)
end
subplot(2,2,3),bar(x3,y3),title('BC');
for i = 1:numel(x3)
    text(x3(i), y3(i) + 0.02, [num2str(y3(i)*100),'%'], 'VerticalAlignment', 'top', 'FontSize', 10,'Rotation',90)
end

%plot the cum plot
cy1 = zeros(1,numel(y1));
for i = 1:numel(x1)
    cy1(i) = y1(i);
    if i ~= 1
        cy1(i) = y1(i) + cy1(i-1); 
    end
end
cy2 = zeros(1,numel(y2));
for i = 1:numel(x2)
    cy2(i) = y2(i);
    if i ~= 1
        cy2(i) = y2(i) + cy2(i-1); 
    end
end
cy3 = zeros(1,numel(y3));
for i = 1:numel(x3)
    cy3(i) = y3(i);
    if i ~= 1
        cy3(i) = y3(i) + cy3(i-1); 
    end
end

figure;
subplot(2,2,1),bar(x1,cy1),title('AB');
for i = 1:numel(x1)
    text(x1(i), cy1(i) + 0.02, [num2str(cy1(i)*100),'%'], 'VerticalAlignment', 'top', 'FontSize', 10,'Rotation',90)
end
subplot(2,2,2),bar(x2,cy2),title('AC');
for i = 1:numel(x2)
    text(x2(i), cy2(i) + 0.02, [num2str(cy2(i)*100),'%'], 'VerticalAlignment', 'top', 'FontSize', 10,'Rotation',90)
end
subplot(2,2,3),bar(x3,cy3),title('BC');
for i = 1:numel(x3)
    text(x3(i), cy3(i) + 0.02, [num2str(cy3(i)*100),'%'], 'VerticalAlignment', 'top', 'FontSize', 10,'Rotation',90)
end



digits(5);
result = calculateResultWithError(3,normalized1,normalized2,normalized3);