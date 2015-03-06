%plot the histogram of the array;
%histogram(array),title('A VS B');
%normalize the data
normalized = (tabulate(array));
normalized_result = normalized(:,[1 3]);
%plot the normalized plot
figure;
x = normalized_result(:,1);
y = normalized_result(:,2)/100;
bar(x,y);