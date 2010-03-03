module KPI
  def sum_new(data)
    if data.respond_to? 'each'
      s=0.0
      data.each do |element|
        s+=element
      end
      s
    end
  end

  def count(data)
    if data.respond_to? 'each'
      s=0
      data.each do |element|
        s+=1
      end
      s
    end
  end

  def average(data)
    #begin time
    start=Time.now.usec
    a=data.sum/data.count
    #end time
    puts "[average]Time: #{Time.now.usec-start}"
    a
  end

  def average_opt(data)
    #begin time
    start=Time.now.usec
    if data.respond_to? 'each'
      n=0
      s=0.0
      data.each do |element|
        s+=element
        n+=1
      end
      #check division by zero
      a=s/n
      #end time
      puts "[average_opt]Time: #{Time.now.usec-start}"
      a
    end
  end

  def harmean(data)
    if data.respond_to? 'each'
      n=0
      s=0.0
      data.each do |element|
        s+=1.0/element
        n+=1
      end
      #check division by zero
      1.0/(s/n)
    end
  end

  def geomean(data)
    if data.respond_to? 'each'
      n=0
      s=1
      data.each do |element|
        s*=element
        n+=1
      end
      #check division by zero
      root(s,n)
    end
  end

  def squareroot(number)
    Math.sqrt(number)
  end

  def product(a,b)
    a*b
  end

  def power(a,b)
    a**b
  end

  def root(number,n)
    number**(1.0/n)
  end

  def round(number)
    if number.respond_to? 'round'
      number.round
    end
  end

  def trunc(number)
    if number.respond_to? 'truncate'
      number.truncate
    end
  end

  def log10(number)
    Math.log10(number)
  end

  def log(number)
    Math.log(number)
  end

  def maximum(data)
    if data.respond_to? 'max'
      data.max
    elsif data.respond_to? 'each'
      max=Float::MIN
      data.each do |element|
        max=element if element>=max
      end
      max
    end
  end

  def minimum(data)
    if data.respond_to? 'min'
      data.min
    elsif data.respond_to? 'each'
      min=Float::MAX
      data.each do |element|
        min=element if element<=min
      end
      min
    end
  end

  def avedev(data)
    if data.respond_to? 'each'
      avg=average_opt(data)
      sum=0.0
      n=0
      data.each do |element|
        sum+=(element-avg).abs
        n+=1
      end
      sum/n
    end
  end

  def stdev(data)
    if data.respond_to? 'each'
      avg=average_opt(data)
      sum=0.0
      n=0
      data.each do |element|
        sum+=(element-avg)**2
        n+=1
      end
      squareroot(sum/(n-1))
    end
  end

  def seriessum(x,n,m,coefficients)
    i=1
    sum=0.0
    coefficients.each do |coeff|
      sum+=coeff*x**(n+(i-1)*m)
      i+=1
    end
    sum
  end

  def subtotal(data, func)
    if data.respond_to? func
      KPI.send(func, data)
    end
  end

  def mediana(data)
    arr=Array.new
    n=0
    data.each do |element|
      n+=1
      arr<<element #if element.is_a? Fixnum
    end
    arr=arr.sort
    n%2!=0 ? arr[(n+1)/2-1] : (arr[n/2-1]+arr[n/2]).to_f/2
  end
end
